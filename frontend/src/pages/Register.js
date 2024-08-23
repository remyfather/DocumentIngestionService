import React, { useState } from "react";
import { useNavigate } from "react-router-dom";

const Register = () => {
  const [url, setUrl] = useState("");
  const [chunkSize, setChunkSize] = useState(500);
  const [chunkOverlap, setChunkOverlap] = useState(50);
  const [divClasses, setDivClasses] = useState("");
  const navigate = useNavigate();

  const handleSubmit = (e) => {
    e.preventDefault();
    const requestBody = {
      url,
      chunkSize,
      chunkOverlap,
      divClasses: divClasses.split(",").map((cls) => cls.trim()),
    };

    fetch("http://localhost:5001/api/rag/save-url-options", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(requestBody),
    }).then(() => navigate("/"));
  };

  return (
    <div>
      <h1>게시글 등록</h1>
      <form onSubmit={handleSubmit}>
        <label>
          URL:
          <input type="text" value={url} onChange={(e) => setUrl(e.target.value)} required />
        </label>
        <label>
          청크 사이즈:
          <input type="number" value={chunkSize} onChange={(e) => setChunkSize(e.target.value)} required />
        </label>
        <label>
          청크 오버랩:
          <input type="number" value={chunkOverlap} onChange={(e) => setChunkOverlap(e.target.value)} required />
        </label>
        <label>
          Div Classes (쉼표로 구분):
          <input type="text" value={divClasses} onChange={(e) => setDivClasses(e.target.value)} required />
        </label>
        <button type="submit">저장</button>
      </form>
    </div>
  );
};

export default Register;