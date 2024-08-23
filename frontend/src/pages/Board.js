import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";

const Board = () => {
  const [posts, setPosts] = useState([]);

  useEffect(() => {
    fetch("http://localhost:5001/api/rag/url-options")  // 백엔드 서버 URL로 수정
      .then((response) => response.json())
      .then((data) => setPosts(data));
  }, []);

  return (
    <div>
      <h1>게시판</h1>
      <Link to="/register">
        <button>게시글 등록하기</button>
      </Link>
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>URL</th>
            <th>상세 보기</th>
          </tr>
        </thead>
        <tbody>
          {posts.map((post) => (
            <tr key={post.id}>
              <td>{post.id}</td>
              <td>{post.url}</td>
              <td>
                <Link to={`/detail/${post.id}`}>상세 보기</Link>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default Board;
