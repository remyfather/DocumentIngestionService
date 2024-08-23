import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Board from "./pages/Board";
import Register from "./pages/Register";
import Detail from "./pages/Detail";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Board />} />
        <Route path="/register" element={<Register />} />
        <Route path="/detail/:id" element={<Detail />} />
      </Routes>
    </Router>
  );
}

export default App;
