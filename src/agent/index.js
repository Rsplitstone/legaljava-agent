const express = require('express');
const app = express();

const port = process.env.PORT || 3000;
const { verifyAndParseRequest, getUserMessage, createTextEvent, createDoneEvent } = require('@copilot-extensions/preview-sdk');
const { Octokit } = require("@octokit/core");

app.use(express.json());

// Adding a simple health check endpoint for testing
app.get('/health', (req, res) => {
  res.status(200).json({ 
    status: 'ok',
    message: 'Legal Java Agent is running'
  });
});

app.post('/agent', async (req, res) => {
  try {
    // Authenticate request using GitHub’s OIDC-based public key verification
    const payload = verifyAndParseRequest(req, process.env.GITHUB_APP_PUBLIC_KEY);
    const userMessage = getUserMessage(payload);
    const userToken = payload.authorization;

    // Validate user identity using Octokit
    const octokit = new Octokit({ auth: userToken });
    const { data: user } = await octokit.request("GET /user");
    console.log(`Processing request from ${user.login}: ${userMessage}`);

    res.write(createTextEvent("Processing your legal query..."));

    // Call the legal query processing module (detailed in Phase 3)
    const result = await require('../legal/retrieval').processLegalQuery(userMessage);
    res.write(createTextEvent(result));
    res.write(createDoneEvent());
    res.end();
  } catch (error) {
    console.error("Authentication or processing error:", error);
    res.write(createTextEvent("An error occurred processing your request."));
    res.write(createDoneEvent());
    res.end();
  }
});

app.listen(port, () => {
  console.log(`LegalJava Agent listening on port ${port}`);
});