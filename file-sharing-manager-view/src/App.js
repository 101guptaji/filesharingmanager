import React, { useState } from 'react';
import FileList from './FileList'; // Assuming FileList component exists
import UploadFile from './UploadFile'; // Assuming UploadFile component exists
import './App.css'; // Include the updated CSS

const App = () => {
  const [refresh, setRefresh] = useState(false);

  const handleUploadSuccess = () => {
    setRefresh(!refresh); // Toggle refresh state to force list update
  };

  return (
    <div className="app-container">
      <div className="slate-heading">
        <h1>File Sharing Manager</h1>
      </div>

      <div className="box-container">
        <div className="box upload-box">
          <h3>Upload a File</h3>
          <UploadFile onUploadSuccess={handleUploadSuccess} />
        </div>

        <div className="box file-list-box">
          <FileList key={refresh} />
        </div>
      </div>
    </div>
  );
};

export default App;
