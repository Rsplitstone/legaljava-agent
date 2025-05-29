import { mkdir } from 'fs/promises';
import { join } from 'path';

const directories = [
  'src/agent/auth',
  'src/agent/controllers',
  'src/agent/events',
  'src/agent/middleware',
  'src/legal/corpus',
  'src/legal/services',
  'src/legal/models',
  'src/llm/chains',
  'src/llm/embeddings',
  'src/llm/models',
  'src/llm/prompts',
  'src/utils',
  'data/labor_code',
  'data/regulations',
  'data/cases',
  'data/vector-db',
  'docs/api',
  'docs/guides',
  'tests/fixtures',
  'tests/unit',
  'tests/integration',
  'tests/e2e'
];

async function createDirectories() {
  try {
    for (const dir of directories) {
      await mkdir(dir, { recursive: true });
      console.log(`Created directory: ${dir}`);
    }
    console.log('All directories created successfully!');
  } catch (error) {
    console.error('Error creating directories:', error);
  }
}

createDirectories();
