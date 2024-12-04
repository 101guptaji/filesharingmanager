import React, { useState } from 'react';
import axios from 'axios';

const UploadFile = ({ onUploadSuccess }) => {
    const [file, setFile] = useState(null);
    const [uploading, setUploading] = useState(false);
    const [progress, setProgress] = useState(0);
    const [message, setMessage] = useState('');
  
    const handleFileChange = (e) => {
      setFile(e.target.files[0]);
      setMessage('');
      setProgress(0);
    };
  
    const handleUpload = () => {
        if (!file) {
          setMessage('Please select a file to upload.');
          return;
        }
      
        setUploading(true);
        const formData = new FormData();
        formData.append('file', file);
      
        axios.post('http://localhost:8080/api/files/upload', formData, {
          onUploadProgress: (progressEvent) => {
            const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total);
            setProgress(percentCompleted);
          },
          timeout: 1200000, // Adjust as needed for large files
        })
        .then((response) => {
          setMessage(response.data);
          setUploading(false);
          onUploadSuccess();
        })
        .catch(() => {
          setMessage('File upload failed. Please try again.');
          setUploading(false);
        });
    };
      
  
    return (
      <div style={{ padding: '20px', border: '1px solid #ccc', margin: '20px 0', borderRadius: '8px' }}>
        <input type="file" onChange={handleFileChange} />
        <button onClick={handleUpload} disabled={uploading}>
          {uploading ? 'Uploading...' : 'Upload'}
        </button>
        {uploading && <p>Uploading: {progress}%</p>}
        {message && <p>{message}</p>}
      </div>
    );
  };
  
  export default UploadFile;
