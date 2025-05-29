export interface QueryRequest {
  query: string;
  userId?: string;
  sessionId?: string;
}

export interface QueryResponse {
  response: string;
  citations: string[];
  sessionId?: string;
  confidence?: number;
}

export interface Message {
  id: string;
  text: string;
  sender: 'user' | 'assistant';
  timestamp: Date;
  citations?: string[];
}

export interface LegalDocument {
  id: number;
  title: string;
  content: string;
  documentType: string;
  sourceUrl?: string;
  createdAt: string;
  updatedAt: string;
}

// Workers' Compensation Types
export interface WorkersCompCase {
  id: number;
  caseNumber: string;
  claimantName: string;
  employerName: string;
  injuryDate: string;
  injuryDescription: string;
  status: 'OPEN' | 'PENDING_REVIEW' | 'CLOSED' | 'SETTLED' | 'LITIGATED';
  adjusterName?: string;
  adjusterId?: string;
  weeklyWage?: number;
  disabilityRating?: number;
  maxMedicalImprovement?: string;
  caseNotes?: string;
  createdAt: string;
  updatedAt: string;
}

export interface AMEReport {
  id: number;
  caseId: number;
  doctorName: string;
  reportDate: string;
  reportType: 'INITIAL' | 'FOLLOWUP' | 'FINAL';
  medicalFindings: string;
  functionalLimitations?: string;
  workRestrictions?: string;
  disabilityRating?: number;
  isPermanentAndStationary?: boolean;
  recommendedTreatment?: string;
  returnToWorkDate?: string;
  reportSummary?: string;
  createdAt: string;
  updatedAt: string;
}

export interface CaseTask {
  id: number;
  caseId: number;
  title: string;
  description?: string;
  type: 'ADMIN' | 'LEGAL' | 'MEDICAL' | 'INVESTIGATION' | 'COMMUNICATION';
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';
  status: 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';
  assignedTo?: string;
  dueDate?: string;
  completedAt?: string;
  createdAt: string;
  updatedAt: string;
}

export interface AMESummaryRequest {
  reportText: string;
  extractDisabilityRating: boolean;
  extractWorkRestrictions: boolean;
  extractTreatmentRecommendations: boolean;
}

export interface AMESummaryResponse {
  summary: string;
  keyFindings: string[];
  disabilityRating?: number;
  workRestrictions?: string[];
  treatmentRecommendations?: string[];
  returnToWorkAssessment?: string;
  confidence: number;
}
