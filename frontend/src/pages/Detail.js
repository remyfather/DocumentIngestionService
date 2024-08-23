import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

const Detail = () => {
  const { id } = useParams();
  const [post, setPost] = useState(null);

  useEffect(() => {
    fetch(`http://localhost:5001/api/rag/url-options/${id}`)
      .then((response) => response.json())
      .then((data) => setPost(data));
  }, [id]);

    const handleChunk = () => {
      fetch(`http://localhost:5001/api/rag/extract/${id}`, {
        method: "POST",
      })
        .then((response) => {
          if (!response.ok) {
            throw new Error("청킹에 실패했습니다.");
          }
          return response.json();
        })
        .then(() => alert("청킹이 완료되었습니다."))
        .catch((error) => alert(error.message));
    };


  if (!post) return <div>로딩 중...</div>;

  return (
    <div>
      <h1>게시글 상세</h1>
      <p><strong>URL:</strong> {post.url}</p>
      <p><strong>청크 사이즈:</strong> {post.chunkSize}</p>
      <p><strong>청크 오버랩:</strong> {post.chunkOverlap}</p>
      <p><strong>Div Classes:</strong> {post.divClasses.join(", ")}</p>
      <button onClick={handleChunk}>청킹하기</button>
    </div>
  );
};

export default Detail;
