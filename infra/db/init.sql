-- Initialize LegalJava database with pgvector extension

-- Enable pgvector extension for vector operations
CREATE EXTENSION IF NOT EXISTS vector;

-- Create legal_documents table
CREATE TABLE IF NOT EXISTS legal_documents (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    content TEXT NOT NULL,
    file_path VARCHAR(1000),
    document_type VARCHAR(100),
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    metadata JSONB,
    embedding vector(1536), -- OpenAI embedding dimension
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create legal_queries table
CREATE TABLE IF NOT EXISTS legal_queries (
    id BIGSERIAL PRIMARY KEY,
    query_text TEXT NOT NULL,
    response TEXT,
    citations TEXT[],
    confidence_score DECIMAL(3,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_legal_documents_title ON legal_documents(title);
CREATE INDEX IF NOT EXISTS idx_legal_documents_type ON legal_documents(document_type);
CREATE INDEX IF NOT EXISTS idx_legal_documents_upload_date ON legal_documents(upload_date);
CREATE INDEX IF NOT EXISTS idx_legal_queries_created_at ON legal_queries(created_at);

-- Create vector index for similarity search
CREATE INDEX IF NOT EXISTS idx_legal_documents_embedding ON legal_documents 
USING ivfflat (embedding vector_cosine_ops) WITH (lists = 100);

-- Create workers_comp_cases table
CREATE TABLE IF NOT EXISTS workers_comp_cases (
    id BIGSERIAL PRIMARY KEY,
    case_number VARCHAR(100) NOT NULL UNIQUE,
    claimant_name VARCHAR(200) NOT NULL,
    employer_name VARCHAR(200) NOT NULL,
    injury_date DATE NOT NULL,
    injury_description TEXT NOT NULL,
    status VARCHAR(50) DEFAULT 'OPEN',
    adjuster_name VARCHAR(200),
    adjuster_id VARCHAR(100),
    weekly_wage DECIMAL(10,2),
    disability_rating DECIMAL(5,2),
    max_medical_improvement DATE,
    case_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create ame_reports table
CREATE TABLE IF NOT EXISTS ame_reports (
    id BIGSERIAL PRIMARY KEY,
    case_id BIGINT NOT NULL REFERENCES workers_comp_cases(id) ON DELETE CASCADE,
    doctor_name VARCHAR(200) NOT NULL,
    specialty VARCHAR(100) NOT NULL,
    examination_date DATE NOT NULL,
    report_content TEXT,
    ai_summary TEXT,
    recommended_disability_rating DECIMAL(5,2),
    work_restrictions TEXT,
    treatment_recommendations TEXT,
    is_final BOOLEAN DEFAULT FALSE,
    file_path VARCHAR(1000) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create case_tasks table
CREATE TABLE IF NOT EXISTS case_tasks (
    id BIGSERIAL PRIMARY KEY,
    case_id BIGINT NOT NULL REFERENCES workers_comp_cases(id) ON DELETE CASCADE,
    title VARCHAR(500) NOT NULL,
    description TEXT,
    task_type VARCHAR(50) NOT NULL,
    priority VARCHAR(20) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    due_date DATE NOT NULL,
    assigned_to VARCHAR(200),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP
);

-- Create indexes for workers comp tables
CREATE INDEX IF NOT EXISTS idx_workers_comp_cases_case_number ON workers_comp_cases(case_number);
CREATE INDEX IF NOT EXISTS idx_workers_comp_cases_claimant_name ON workers_comp_cases(claimant_name);
CREATE INDEX IF NOT EXISTS idx_workers_comp_cases_employer_name ON workers_comp_cases(employer_name);
CREATE INDEX IF NOT EXISTS idx_workers_comp_cases_status ON workers_comp_cases(status);
CREATE INDEX IF NOT EXISTS idx_workers_comp_cases_injury_date ON workers_comp_cases(injury_date);
CREATE INDEX IF NOT EXISTS idx_workers_comp_cases_adjuster_name ON workers_comp_cases(adjuster_name);

CREATE INDEX IF NOT EXISTS idx_ame_reports_case_id ON ame_reports(case_id);
CREATE INDEX IF NOT EXISTS idx_ame_reports_doctor_name ON ame_reports(doctor_name);
CREATE INDEX IF NOT EXISTS idx_ame_reports_specialty ON ame_reports(specialty);
CREATE INDEX IF NOT EXISTS idx_ame_reports_examination_date ON ame_reports(examination_date);
CREATE INDEX IF NOT EXISTS idx_ame_reports_is_final ON ame_reports(is_final);

CREATE INDEX IF NOT EXISTS idx_case_tasks_case_id ON case_tasks(case_id);
CREATE INDEX IF NOT EXISTS idx_case_tasks_status ON case_tasks(status);
CREATE INDEX IF NOT EXISTS idx_case_tasks_priority ON case_tasks(priority);
CREATE INDEX IF NOT EXISTS idx_case_tasks_due_date ON case_tasks(due_date);
CREATE INDEX IF NOT EXISTS idx_case_tasks_assigned_to ON case_tasks(assigned_to);
CREATE INDEX IF NOT EXISTS idx_case_tasks_task_type ON case_tasks(task_type);

-- Insert sample data for testing
INSERT INTO legal_documents (title, content, document_type, metadata) VALUES
('Sample Contract', 'This is a sample legal document for testing purposes.', 'CONTRACT', '{"tags": ["sample", "test"]}'),
('Privacy Policy Template', 'Sample privacy policy content for demonstration.', 'POLICY', '{"tags": ["privacy", "template"]}')
ON CONFLICT DO NOTHING;

-- Insert sample workers comp data
INSERT INTO workers_comp_cases (case_number, claimant_name, employer_name, injury_date, injury_description, status, adjuster_name, weekly_wage) VALUES
('WC-2024-001', 'John Smith', 'ABC Manufacturing', '2024-01-15', 'Back injury from lifting heavy machinery', 'OPEN', 'Jane Adjuster', 1200.00),
('WC-2024-002', 'Maria Garcia', 'XYZ Construction', '2024-02-20', 'Hand injury from power tool accident', 'PENDING_REVIEW', 'Bob Claims', 950.00),
('WC-2024-003', 'Robert Johnson', 'Tech Solutions Inc', '2024-03-10', 'Repetitive stress injury - carpal tunnel', 'OPEN', 'Jane Adjuster', 1500.00)
ON CONFLICT (case_number) DO NOTHING;

-- Insert sample AME reports
INSERT INTO ame_reports (case_id, doctor_name, specialty, examination_date, report_content, is_final, file_path) VALUES
(1, 'Dr. Michael Thompson', 'Orthopedic Surgery', '2024-04-15', 'Patient presents with chronic lower back pain following workplace injury. Examination reveals moderate disc herniation at L4-L5 level.', true, '/reports/ame_001_thompson.pdf'),
(2, 'Dr. Sarah Williams', 'Hand Surgery', '2024-05-01', 'Examination of right hand shows partial tendon damage with functional limitations in grip strength.', false, '/reports/ame_002_williams.pdf')
ON CONFLICT DO NOTHING;

-- Insert sample case tasks
INSERT INTO case_tasks (case_id, title, description, task_type, priority, due_date, assigned_to) VALUES
(1, 'Review Initial Medical Records', 'Review and analyze all initial medical documentation for case WC-2024-001', 'MEDICAL_REVIEW', 'HIGH', '2024-06-01', 'legal.assistant@legaljava.com'),
(1, 'Calculate Temporary Disability Benefits', 'Calculate TD benefits based on current weekly wage and CA regulations', 'BENEFIT_CALCULATION', 'MEDIUM', '2024-06-05', 'benefits.calculator@legaljava.com'),
(2, 'Schedule AME Follow-up', 'Schedule follow-up AME appointment for final rating determination', 'AME_SCHEDULING', 'HIGH', '2024-06-03', 'scheduling@legaljava.com'),
(3, 'Deadline Compliance Check', 'Verify all statutory deadlines are being met for ergonomic injury case', 'DEADLINE_COMPLIANCE', 'URGENT', '2024-05-30', 'compliance@legaljava.com')
ON CONFLICT DO NOTHING;
