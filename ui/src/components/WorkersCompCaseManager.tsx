import React, { useState, useEffect } from 'react';
import { legalService } from '../services/api';
import type { WorkersCompCase } from '../types';

interface CaseFormData {
  caseNumber: string;
  claimantName: string;
  employerName: string;
  injuryDate: string;
  injuryDescription: string;
  adjusterName: string;
  weeklyWage: string;
}

const WorkersCompCaseManager: React.FC = () => {
  const [cases, setCases] = useState<WorkersCompCase[]>([]);
  const [selectedCase, setSelectedCase] = useState<WorkersCompCase | null>(null);
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState<string>('');
  
  const [formData, setFormData] = useState<CaseFormData>({
    caseNumber: '',
    claimantName: '',
    employerName: '',
    injuryDate: '',
    injuryDescription: '',
    adjusterName: '',
    weeklyWage: ''
  });

  useEffect(() => {
    fetchCases();
  }, []);
  const fetchCases = async () => {
    try {
      setLoading(true);
      const data = await legalService.getAllCases();
      setCases(data);
    } catch (error) {
      console.error('Error fetching cases:', error);
    } finally {
      setLoading(false);
    }
  };  const handleCreateCase = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    try {
      const caseData = {
        ...formData,
        weeklyWage: formData.weeklyWage ? parseFloat(formData.weeklyWage) : undefined,
        status: 'OPEN' as const
      };
      
      await legalService.createCase(caseData);
      setShowCreateForm(false);
      setFormData({
        caseNumber: '',
        claimantName: '',
        employerName: '',
        injuryDate: '',
        injuryDescription: '',
        adjusterName: '',
        weeklyWage: ''
      });
      fetchCases();
    } catch (error) {
      console.error('Error creating case:', error);
    }
  };
  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };
  const filteredCases = cases.filter((case_: WorkersCompCase) => {
    const matchesSearch = 
      case_.caseNumber.toLowerCase().includes(searchTerm.toLowerCase()) ||
      case_.claimantName.toLowerCase().includes(searchTerm.toLowerCase()) ||
      case_.employerName.toLowerCase().includes(searchTerm.toLowerCase());
    
    const matchesStatus = !statusFilter || case_.status === statusFilter;
    
    return matchesSearch && matchesStatus;
  });

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'OPEN': return 'bg-green-100 text-green-800';
      case 'PENDING_REVIEW': return 'bg-yellow-100 text-yellow-800';
      case 'CLOSED': return 'bg-gray-100 text-gray-800';
      case 'SETTLED': return 'bg-blue-100 text-blue-800';
      case 'LITIGATED': return 'bg-red-100 text-red-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString();
  };

  const formatCurrency = (amount: number | undefined) => {
    return amount ? `$${amount.toLocaleString()}` : 'N/A';
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-blue-500"></div>
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-900">Workers' Compensation Cases</h1>
        <button
          onClick={() => setShowCreateForm(true)}
          className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg flex items-center"
        >
          <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
          </svg>
          New Case
        </button>
      </div>

      {/* Search and Filters */}
      <div className="mb-6 flex flex-col sm:flex-row gap-4">
        <div className="flex-1">
          <input
            type="text"
            placeholder="Search by case number, claimant, or employer..."
            value={searchTerm}
            onChange={(e: React.ChangeEvent<HTMLInputElement>) => setSearchTerm(e.target.value)}
            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>
        <div>
          <label htmlFor="statusFilter" className="sr-only">
            Filter by Status
          </label>
          <select
            id="statusFilter"
            value={statusFilter}
            onChange={(e: React.ChangeEvent<HTMLSelectElement>) => setStatusFilter(e.target.value)}
            className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          >
            <option value="">All Statuses</option>
            <option value="OPEN">Open</option>
            <option value="PENDING_REVIEW">Pending Review</option>
            <option value="CLOSED">Closed</option>
            <option value="SETTLED">Settled</option>
            <option value="LITIGATED">Litigated</option>
          </select>
        </div>
      </div>

      {/* Cases Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {filteredCases.map((case_: WorkersCompCase) => (
          <div
            key={case_.id}
            className="bg-white rounded-lg shadow-md hover:shadow-lg transition-shadow cursor-pointer border border-gray-200"
            onClick={() => setSelectedCase(case_)}
          >
            <div className="p-6">
              <div className="flex justify-between items-start mb-4">
                <h3 className="text-lg font-semibold text-gray-900">{case_.caseNumber}</h3>
                <span className={`px-2 py-1 rounded-full text-xs font-medium ${getStatusColor(case_.status)}`}>
                  {case_.status}
                </span>
              </div>
              
              <div className="space-y-2 text-sm text-gray-600">
                <div>
                  <span className="font-medium">Claimant:</span> {case_.claimantName}
                </div>
                <div>
                  <span className="font-medium">Employer:</span> {case_.employerName}
                </div>
                <div>
                  <span className="font-medium">Injury Date:</span> {formatDate(case_.injuryDate)}
                </div>
                <div>
                  <span className="font-medium">Weekly Wage:</span> {formatCurrency(case_.weeklyWage)}
                </div>
                {case_.adjusterName && (
                  <div>
                    <span className="font-medium">Adjuster:</span> {case_.adjusterName}
                  </div>
                )}
              </div>
              
              <div className="mt-4 text-xs text-gray-500">
                Updated: {formatDate(case_.updatedAt)}
              </div>
            </div>
          </div>
        ))}
      </div>

      {filteredCases.length === 0 && (
        <div className="text-center py-12">
          <div className="text-gray-500 text-lg">No cases found</div>
          <div className="text-gray-400 text-sm mt-2">
            {searchTerm || statusFilter ? 'Try adjusting your search criteria' : 'Create your first case to get started'}
          </div>
        </div>
      )}

      {/* Create Case Modal */}
      {showCreateForm && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg max-w-md w-full max-h-[90vh] overflow-y-auto">
            <div className="p-6">
              <div className="flex justify-between items-center mb-6">                <h2 className="text-xl font-bold text-gray-900">Create New Case</h2>
                <button
                  onClick={() => setShowCreateForm(false)}
                  className="text-gray-400 hover:text-gray-600"
                  title="Close create case form"
                  aria-label="Close create case form"
                >
                  <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
              </div>

              <form onSubmit={handleCreateCase} className="space-y-4">                <div>
                  <label htmlFor="caseNumber" className="block text-sm font-medium text-gray-700 mb-1">
                    Case Number *
                  </label>
                  <input
                    type="text"
                    id="caseNumber"
                    name="caseNumber"
                    value={formData.caseNumber}
                    onChange={handleInputChange}
                    required
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  />
                </div>                <div>
                  <label htmlFor="claimantName" className="block text-sm font-medium text-gray-700 mb-1">
                    Claimant Name *
                  </label>
                  <input
                    type="text"
                    id="claimantName"
                    name="claimantName"
                    value={formData.claimantName}
                    onChange={handleInputChange}
                    required
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  />
                </div>                <div>
                  <label htmlFor="employerName" className="block text-sm font-medium text-gray-700 mb-1">
                    Employer Name *
                  </label>
                  <input
                    type="text"
                    id="employerName"
                    name="employerName"
                    value={formData.employerName}
                    onChange={handleInputChange}
                    required
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  />
                </div>                <div>
                  <label htmlFor="injuryDate" className="block text-sm font-medium text-gray-700 mb-1">
                    Injury Date *
                  </label>
                  <input
                    type="date"
                    id="injuryDate"
                    name="injuryDate"
                    value={formData.injuryDate}
                    onChange={handleInputChange}
                    required
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  />
                </div>                <div>
                  <label htmlFor="injuryDescription" className="block text-sm font-medium text-gray-700 mb-1">
                    Injury Description *
                  </label>
                  <textarea
                    id="injuryDescription"
                    name="injuryDescription"
                    value={formData.injuryDescription}
                    onChange={handleInputChange}
                    required
                    rows={3}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  />
                </div>                <div>
                  <label htmlFor="adjusterName" className="block text-sm font-medium text-gray-700 mb-1">
                    Adjuster Name
                  </label>
                  <input
                    type="text"
                    id="adjusterName"
                    name="adjusterName"
                    value={formData.adjusterName}
                    onChange={handleInputChange}
                    placeholder="Enter adjuster name"
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  />
                </div>                <div>
                  <label htmlFor="weeklyWage" className="block text-sm font-medium text-gray-700 mb-1">
                    Weekly Wage ($)
                  </label>
                  <input
                    type="number"
                    id="weeklyWage"
                    name="weeklyWage"
                    value={formData.weeklyWage}
                    onChange={handleInputChange}
                    step="0.01"
                    min="0"
                    placeholder="Enter weekly wage amount"
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  />
                </div>

                <div className="flex gap-3 pt-4">
                  <button
                    type="button"
                    onClick={() => setShowCreateForm(false)}
                    className="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50"
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    className="flex-1 px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
                  >
                    Create Case
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}

      {/* Case Detail Modal */}
      {selectedCase && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg max-w-2xl w-full max-h-[90vh] overflow-y-auto">
            <div className="p-6">
              <div className="flex justify-between items-center mb-6">                <h2 className="text-xl font-bold text-gray-900">{selectedCase.caseNumber}</h2>
                <button
                  onClick={() => setSelectedCase(null)}
                  className="text-gray-400 hover:text-gray-600"
                  title="Close case details"
                  aria-label="Close case details modal"
                >
                  <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
              </div>

              <div className="space-y-6">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <h3 className="text-sm font-medium text-gray-700 mb-2">Case Information</h3>
                    <div className="space-y-2 text-sm">
                      <div><span className="font-medium">Status:</span> 
                        <span className={`ml-2 px-2 py-1 rounded-full text-xs ${getStatusColor(selectedCase.status)}`}>
                          {selectedCase.status}
                        </span>
                      </div>
                      <div><span className="font-medium">Claimant:</span> {selectedCase.claimantName}</div>
                      <div><span className="font-medium">Employer:</span> {selectedCase.employerName}</div>
                      <div><span className="font-medium">Injury Date:</span> {formatDate(selectedCase.injuryDate)}</div>
                      {selectedCase.adjusterName && (
                        <div><span className="font-medium">Adjuster:</span> {selectedCase.adjusterName}</div>
                      )}
                    </div>
                  </div>
                  
                  <div>
                    <h3 className="text-sm font-medium text-gray-700 mb-2">Financial Information</h3>
                    <div className="space-y-2 text-sm">
                      <div><span className="font-medium">Weekly Wage:</span> {formatCurrency(selectedCase.weeklyWage)}</div>
                      {selectedCase.disabilityRating && (
                        <div><span className="font-medium">Disability Rating:</span> {selectedCase.disabilityRating}%</div>
                      )}
                      {selectedCase.maxMedicalImprovement && (
                        <div><span className="font-medium">MMI Date:</span> {formatDate(selectedCase.maxMedicalImprovement)}</div>
                      )}
                    </div>
                  </div>
                </div>

                <div>
                  <h3 className="text-sm font-medium text-gray-700 mb-2">Injury Description</h3>
                  <p className="text-sm text-gray-600 bg-gray-50 p-3 rounded-md">
                    {selectedCase.injuryDescription}
                  </p>
                </div>

                {selectedCase.caseNotes && (
                  <div>
                    <h3 className="text-sm font-medium text-gray-700 mb-2">Case Notes</h3>
                    <p className="text-sm text-gray-600 bg-gray-50 p-3 rounded-md">
                      {selectedCase.caseNotes}
                    </p>
                  </div>
                )}

                <div className="text-xs text-gray-500 pt-4 border-t">
                  <div>Created: {formatDate(selectedCase.createdAt)}</div>
                  <div>Updated: {formatDate(selectedCase.updatedAt)}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default WorkersCompCaseManager;
