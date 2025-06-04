import axios from 'axios';
import { useDropzone } from 'react-dropzone';

interface FileDropProps {
  onFiles?: (files: File[]) => void;
}

export default function FileDrop({ onFiles }: FileDropProps) {
  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    multiple: true,
    onDrop: async (acceptedFiles: File[]) => {
      try {
        const formData = new FormData();
        acceptedFiles.forEach((file: File) => formData.append('file', file));

        const response = await axios.post('/api/files', formData, {
          headers: {
            'Content-Type': 'multipart/form-data',
          },
        });

        console.log('File upload successful:', response.data);
      } catch (error) {
        console.error('Error uploading files:', error);
      }

      if (onFiles) {
        onFiles(acceptedFiles);
      }
    },
  });

  return (
    <div
      {...getRootProps()}
      className={`file-drop-container ${isDragActive ? 'file-drop-active' : ''}`}
    >
      <input {...getInputProps()} className="file-drop-input" />
      <p>Drag & drop QME reports here, or click to select files</p>
    </div>
  );
}
