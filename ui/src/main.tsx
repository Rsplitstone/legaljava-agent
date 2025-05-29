import React from 'react';
import ReactDOM from 'react-dom/client';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Layout from './components/Layout';
import Schedule from './components/Schedule';
import CaseWorkspace from './components/CaseWorkspace';
import Analytics from './components/Analytics';

const qc = new QueryClient();

const root = ReactDOM.createRoot(document.getElementById('root')!);
root.render(
  <QueryClientProvider client={qc}>
    <BrowserRouter>
      <Routes>
        <Route element={<Layout />}>
          <Route index element={<Schedule />} />
          <Route path="case/:id/*" element={<CaseWorkspace />} />
          <Route path="analytics" element={<Analytics />} />
        </Route>
      </Routes>
    </BrowserRouter>
  </QueryClientProvider>
);
