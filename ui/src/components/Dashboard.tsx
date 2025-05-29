import React, { useState, useEffect } from 'react';
import DocumentUpload from './DocumentUpload';
import WorkersCompCaseManager from './WorkersCompCaseManager';
import ChatInterface from './ChatInterface';

interface DashboardStats {
  totalCases: number;
  pendingTasks: number;
  recentUploads: number;
}

const Dashboard: React.FC = () => {
  const [stats, setStats] = useState<DashboardStats>({
    totalCases: 0,
    pendingTasks: 0,
    recentUploads: 0
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchStats = async () => {
      try {
        // Mock data for now - replace with actual API calls
        setStats({
          totalCases: 15,
          pendingTasks: 8,
          recentUploads: 3
        });
      } catch (error) {
        console.error('Error fetching dashboard stats:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchStats();
  }, []);

  const handleUploadAME = () => {
    console.log('Upload AME Report clicked');
  };

  const handleCalculateBenefits = () => {
    console.log('Calculate Benefits clicked');
  };

  const handleAskAI = () => {
    console.log('Ask AI Question clicked');
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-blue-500"></div>
      </div>
    );
  }

  return (
    <div className="dashboard">
      <aside className="navigation">
        <h2>Navigation</h2>
        <ul>
          <li>Cases</li>
          <li>Views</li>
          <li>Search</li>
        </ul>
      </aside>

      <main className="work-canvas">
        <DocumentUpload />
        <WorkersCompCaseManager />
        <div className="quick-actions">
          <button onClick={handleCalculateBenefits}>Calculate Benefits</button>
          <button onClick={handleAskAI}>Ask AI Question</button>
        </div>
      </main>

      <aside className="right-rail">
        <ChatInterface />
      </aside>
    </div>
  );
};

export default Dashboard;
