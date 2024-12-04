import React, { useEffect, useState } from 'react';
import axios from 'axios';
import './FileList.css'; // Assuming CSS exists for styling

const FileList = () => {
  const [files, setFiles] = useState([]);

  const fetchFiles = () => {
    axios.get('http://localhost:8080/api/files/list')
      .then((response) => setFiles(response.data))
      .catch(() => console.log('Error fetching files'));
  };

  useEffect(() => {
    fetchFiles();
  }, []);

  const handleDelete = (uuid) => {
    axios.delete(`http://localhost:8080/api/files/delete/${uuid}`)
      .then(() => {
        alert('File deleted successfully');
        fetchFiles();
      })
      .catch(() => alert('Failed to delete file'));
  };

  const handleCopyLink = (link) => {
    navigator.clipboard.writeText(`http://localhost:8080/api/files/download/${link}`);
    alert('Link copied to clipboard!');
  };

  return (
    <div style={{ margin: '20px 0' }}>
      <h3>Uploaded Files</h3>
      {files.length > 0 ? (
        <table>
          <thead>
            <tr>
              <th>File Name</th>
              <th>Upload Time</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {files.map((file) => (
              <tr key={file.downloadLink}>
                <td>{file.fileName}</td>
                <td>{file.uploadTime}</td>
                <td>
                  <button 
                  className='copy'
                  onClick={() => handleCopyLink(file.downloadLink)}>
                    Copy Link
                  </button>
                  <button 
                    className='b2'
                    onClick={() => handleDelete(file.downloadLink)}>
                    Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : (
        <p>No files uploaded yet.</p>
      )}
    </div>
  );
};

export default FileList;
