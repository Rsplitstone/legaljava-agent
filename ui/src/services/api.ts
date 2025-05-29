import axios from 'axios';
import { 
  QueryRequest, 
  QueryResponse, 
  LegalDocument, 
  WorkersCompCase, 
  AMEReport, 
  CaseTask, 
  AMESummaryRequest, 
  AMESummaryResponse 
} from '../types';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Mock data for testing frontend integration
const MOCK_CASES: WorkersCompCase[] = [
  {
    id: 1,
    caseNumber: "WC-2024-001",
    claimantName: "John Smith",
    employerName: "ABC Manufacturing",
    injuryDate: "2024-01-15",
    injuryDescription: "Lower back injury from lifting heavy machinery",
    status: "OPEN",
    adjusterName: "Sarah Johnson",
    weeklyWage: 850.00,
    disabilityRating: 15,
    maxMedicalImprovement: "2024-08-15",
    caseNotes: "Claimant is responding well to physical therapy. Expected to return to light duty soon.",
    createdAt: "2024-01-16",
    updatedAt: "2024-05-20"
  },
  {
    id: 2,
    caseNumber: "WC-2024-002",
    claimantName: "Maria Garcia",
    employerName: "City Hospital",
    injuryDate: "2024-02-03",
    injuryDescription: "Repetitive strain injury in right wrist from computer work",
    status: "PENDING_REVIEW",
    adjusterName: "Mike Rodriguez",
    weeklyWage: 1200.00,
    disabilityRating: 8,
    caseNotes: "Awaiting specialist evaluation for carpal tunnel syndrome.",
    createdAt: "2024-02-04",
    updatedAt: "2024-05-18"
  },
  {
    id: 3,
    caseNumber: "WC-2024-003",
    claimantName: "Robert Johnson",
    employerName: "Construction Plus LLC",
    injuryDate: "2024-03-22",
    injuryDescription: "Shoulder dislocation from fall at construction site",
    status: "SETTLED",
    adjusterName: "Lisa Chen",
    weeklyWage: 950.00,
    disabilityRating: 25,
    maxMedicalImprovement: "2024-07-22",
    caseNotes: "Case settled for $45,000. Claimant has returned to work with restrictions.",
    createdAt: "2024-03-23",
    updatedAt: "2024-05-15"
  }
];

const USE_MOCK_DATA = true; // Set to false when backend is ready

export const legalService = {
  // Chat endpoints
  async processQuery(request: QueryRequest): Promise<QueryResponse> {
    const response = await api.post('/chat/query', request);
    return response.data;
  },

  async getUserHistory(userId: string): Promise<any[]> {
    const response = await api.get(`/chat/history/${userId}`);
    return response.data;
  },

  async getSessionQueries(sessionId: string): Promise<any[]> {
    const response = await api.get(`/chat/session/${sessionId}`);
    return response.data;
  },

  // Document endpoints
  async getAllDocuments(): Promise<LegalDocument[]> {
    const response = await api.get('/documents');
    return response.data;
  },

  async getDocumentById(id: number): Promise<LegalDocument> {
    const response = await api.get(`/documents/${id}`);
    return response.data;
  },

  async searchDocuments(keyword: string): Promise<LegalDocument[]> {
    const response = await api.get(`/documents/search?keyword=${encodeURIComponent(keyword)}`);
    return response.data;
  },

  async uploadDocument(title: string, type: string, file: File): Promise<LegalDocument> {
    const formData = new FormData();
    formData.append('title', title);
    formData.append('type', type);
    formData.append('file', file);

    const response = await api.post('/documents/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  },

  async createDocument(document: Omit<LegalDocument, 'id' | 'createdAt' | 'updatedAt'>): Promise<LegalDocument> {
    const response = await api.post('/documents', document);
    return response.data;
  },
  async deleteDocument(id: number): Promise<void> {
    await api.delete(`/documents/${id}`);
  },

  // Workers' Compensation Case endpoints
  async getAllCases(): Promise<WorkersCompCase[]> {
    if (USE_MOCK_DATA) {
      return MOCK_CASES;
    }
    const response = await api.get('/workers-comp-cases');
    return response.data;
  },
  async getCaseById(id: number): Promise<WorkersCompCase> {
    if (USE_MOCK_DATA) {
      const foundCase = MOCK_CASES.find(caseItem => caseItem.id === id);
      if (!foundCase) {
        throw new Error(`Workers compensation case with ID ${id} not found`);
      }
      return foundCase;
    }
    const response = await api.get(`/workers-comp-cases/${id}`);
    return response.data;
  },

  async createCase(caseData: Omit<WorkersCompCase, 'id' | 'createdAt' | 'updatedAt'>): Promise<WorkersCompCase> {
    const response = await api.post('/workers-comp-cases', caseData);
    return response.data;
  },

  async updateCase(id: number, caseData: Partial<WorkersCompCase>): Promise<WorkersCompCase> {
    const response = await api.put(`/workers-comp-cases/${id}`, caseData);
    return response.data;
  },

  async deleteCase(id: number): Promise<void> {
    await api.delete(`/workers-comp-cases/${id}`);
  },

  async searchCases(params: { keyword?: string; status?: string; injuryDateFrom?: string; injuryDateTo?: string }): Promise<WorkersCompCase[]> {
    const queryParams = new URLSearchParams();
    if (params.keyword) queryParams.append('keyword', params.keyword);
    if (params.status) queryParams.append('status', params.status);
    if (params.injuryDateFrom) queryParams.append('injuryDateFrom', params.injuryDateFrom);
    if (params.injuryDateTo) queryParams.append('injuryDateTo', params.injuryDateTo);
    
    const response = await api.get(`/workers-comp-cases/search?${queryParams.toString()}`);
    return response.data;
  },

  async calculateBenefits(id: number): Promise<any> {
    const response = await api.get(`/workers-comp-cases/${id}/benefits`);
    return response.data;
  },

  // AME Report endpoints
  async getAMEReportsByCase(caseId: number): Promise<AMEReport[]> {
    const response = await api.get(`/ame-reports/case/${caseId}`);
    return response.data;
  },

  async getAMEReportById(id: number): Promise<AMEReport> {
    const response = await api.get(`/ame-reports/${id}`);
    return response.data;
  },

  async createAMEReport(reportData: Omit<AMEReport, 'id' | 'createdAt' | 'updatedAt'>): Promise<AMEReport> {
    const response = await api.post('/ame-reports', reportData);
    return response.data;
  },

  async updateAMEReport(id: number, reportData: Partial<AMEReport>): Promise<AMEReport> {
    const response = await api.put(`/ame-reports/${id}`, reportData);
    return response.data;
  },

  async deleteAMEReport(id: number): Promise<void> {
    await api.delete(`/ame-reports/${id}`);
  },

  async summarizeAMEReport(request: AMESummaryRequest): Promise<AMESummaryResponse> {
    const response = await api.post('/ame-reports/summarize', request);
    return response.data;
  },

  // Case Task endpoints
  async getTasksByCase(caseId: number): Promise<CaseTask[]> {
    const response = await api.get(`/case-tasks/case/${caseId}`);
    return response.data;
  },

  async getTaskById(id: number): Promise<CaseTask> {
    const response = await api.get(`/case-tasks/${id}`);
    return response.data;
  },

  async createTask(taskData: Omit<CaseTask, 'id' | 'createdAt' | 'updatedAt'>): Promise<CaseTask> {
    const response = await api.post('/case-tasks', taskData);
    return response.data;
  },

  async updateTask(id: number, taskData: Partial<CaseTask>): Promise<CaseTask> {
    const response = await api.put(`/case-tasks/${id}`, taskData);
    return response.data;
  },

  async deleteTask(id: number): Promise<void> {
    await api.delete(`/case-tasks/${id}`);
  },

  async getTasksByAssignee(assignee: string): Promise<CaseTask[]> {
    const response = await api.get(`/case-tasks/assignee/${assignee}`);
    return response.data;
  },

  async getOverdueTasks(): Promise<CaseTask[]> {
    const response = await api.get('/case-tasks/overdue');
    return response.data;
  },

  async bulkUpdateTasks(taskIds: number[], updates: Partial<CaseTask>): Promise<CaseTask[]> {
    const response = await api.put('/case-tasks/bulk-update', { taskIds, updates });
    return response.data;
  },

  // Health check
  async healthCheck(): Promise<string> {
    const response = await api.get('/chat/health');
    return response.data;
  },
};

// Simple upload function for DocumentUpload component
export const uploadDocument = async (formData: FormData): Promise<LegalDocument> => {
  const response = await api.post('/documents/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
  return response.data;
};

export default legalService;
