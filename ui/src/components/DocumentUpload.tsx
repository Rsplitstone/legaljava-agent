import React, { useState, useCallback } from 'react';
import { uploadDocument } from '../services/api';

interface DocumentUploadProps {
  onUploadSuccess?: (documentId: string) => void;
}

const ProgressBar: React.FC<{ progress: number }> = ({ progress }) => {
  return (
    <div className="w-full bg-gray-200 rounded-full h-2 relative overflow-hidden">
      <div
        className={`absolute top-0 left-0 bg-blue-600 h-2 rounded-full transition-all duration-300 ${
          progress === 0 ? 'w-0' :
          progress <= 10 ? 'w-1/12' :
          progress <= 20 ? 'w-2/12' :
          progress <= 30 ? 'w-3/12' :
          progress <= 40 ? 'w-4/12' :
          progress <= 50 ? 'w-5/12' :
          progress <= 60 ? 'w-6/12' :
          progress <= 70 ? 'w-7/12' :
          progress <= 80 ? 'w-8/12' :
          progress <= 90 ? 'w-9/12' :
          progress < 100 ? 'w-10/12' : 'w-full'
        }`}
        role="progressbar"
        aria-label={`Upload progress: ${progress}%`}
        data-testid="progress-fill"
      />
    </div>
  );
};

const DocumentUpload: React.FC<DocumentUploadProps> = ({ onUploadSuccess }) => {
  const [isUploading, setIsUploading] = useState(false);
  const [dragActive, setDragActive] = useState(false);
  const [uploadProgress, setUploadProgress] = useState<number | null>(null);

  const handleFiles = useCallback(async (files: FileList | null) => {
    if (!files || files.length === 0) return;

    const file = files[0];
    if (!file.name.toLowerCase().endsWith('.pdf')) {
      alert('Please upload a PDF file');
      return;
    }

    setIsUploading(true);
    setUploadProgress(0);

    try {
      const formData = new FormData();
      formData.append('file', file);

      // Simulate progress for better UX
      const progressInterval = setInterval(() => {
        setUploadProgress(prev => {
          if (prev === null) return 10;
          if (prev >= 90) return prev;
          return prev + 10;
        });
      }, 200);

      const response = await uploadDocument(formData);
      
      clearInterval(progressInterval);
      setUploadProgress(100);
      
      setTimeout(() => {
        setUploadProgress(null);
        setIsUploading(false);
        onUploadSuccess?.(response.id.toString());
      }, 1000);

    } catch (error) {
      console.error('Upload failed:', error);
      setIsUploading(false);
      setUploadProgress(null);
      alert('Upload failed. Please try again.');
    }
  }, [onUploadSuccess]);

  const handleDrag = useCallback((e: React.DragEvent) => {
    e.preventDefault();
    e.stopPropagation();
    if (e.type === 'dragenter' || e.type === 'dragover') {
      setDragActive(true);
    } else if (e.type === 'dragleave') {
      setDragActive(false);
    }
  }, []);

  const handleDrop = useCallback((e: React.DragEvent) => {
    e.preventDefault();
    e.stopPropagation();
    setDragActive(false);
    handleFiles(e.dataTransfer.files);
  }, [handleFiles]);

  const handleFileInput = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
    handleFiles(e.target.files);
  }, [handleFiles]);
  return (
    <div className="w-full max-w-md mx-auto">
      <label 
        htmlFor="document-upload"
        className={`relative border-2 border-dashed rounded-lg p-8 text-center transition-colors cursor-pointer block ${
          dragActive
            ? 'border-blue-400 bg-blue-50'
            : 'border-gray-300 hover:border-gray-400'
        } ${isUploading ? 'pointer-events-none opacity-50' : ''}`}
        onDragEnter={handleDrag}
        onDragLeave={handleDrag}
        onDragOver={handleDrag}
        onDrop={handleDrop}
      >
        <input
          id="document-upload"
          type="file"
          accept=".pdf"
          onChange={handleFileInput}
          className="absolute inset-0 w-full h-full opacity-0 cursor-pointer"
          disabled={isUploading}
          aria-label="Upload PDF document"
        />
        
        <div className="space-y-4">
          <div className="mx-auto w-12 h-12 text-gray-400">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"
              />
            </svg>
          </div>
          
          <div>
            <p className="text-lg font-medium text-gray-900">
              {isUploading ? 'Uploading...' : 'Upload Legal Document'}
            </p>            <p className="text-sm text-gray-500">
              Drag and drop a PDF file here, or click to select
            </p>
          </div>
          
          {uploadProgress !== null && (
            <ProgressBar progress={uploadProgress} />
          )}
        </div>
      </label>
      
      <p className="mt-2 text-xs text-gray-500 text-center">
        Supported format: PDF (max 10MB)
      </p>
    </div>
  );
};

export default DocumentUpload;
