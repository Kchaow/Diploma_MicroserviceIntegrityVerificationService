import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Main from './pages/Main/Main';
import VerificationGraph from './pages/VerificationGraph/VerificationGraph';
import ChangeGraphsList from './pages/ChangeGraphsList/ChangeGraphsList';
import ChangeGraph from './pages/ChangeGraph/ChangeGraph';
import Layout from './components/Layout/Layout';

function App() {
  return (
    <BrowserRouter>
      <Layout>
        <Routes>
          <Route path="/" element={<Main />} />
          <Route path="/verification-graph" element={<VerificationGraph />} />
          <Route path="/change-graph" element={<ChangeGraphsList />}>
            
          </Route>
          <Route path="/change-graph/:id" element={<ChangeGraph />}/>
        </Routes>
      </Layout>
    </BrowserRouter>
  );
}

export default App;