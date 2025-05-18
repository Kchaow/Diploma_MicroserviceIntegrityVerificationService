import React, { useState, useEffect, useRef } from 'react';
import './ChangeGraphsList.css';
import DropdownForm from './DropdownForm';
import * as api from '../../api/MicroserviceVerificationAPI';

function ChangeGraphsList() {
  const [graphsData, setGraphsData] = useState([]);
  const graphsDataRef = useRef(graphsData);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  function refreshList() {
    api.getChangeGraphsList().then(data => {
      console.log(data);
      data.sort((f, s) => new Date(s.dateTime) - new Date(f.dateTime))
      setGraphsData(data);
      graphsDataRef.current = graphsData;
      setLoading(false);
    })
      .catch(err => console.log(err.message));
  }

  useEffect(() => {
    refreshList();
  }, []);

  if (loading) return <div className="loading">Loading...</div>;
  if (error) return <div className="error">Error: {error}</div>;

  return (
    <div className="app-container">
      <h1>Графы изменений</h1>
      <DropdownForm refreshCallback={refreshList} />
      <div className="graphs-list">
        {graphsData.map((graph) => (
          <GraphPanel key={graph.id} data={graph} />
        ))}
      </div>
    </div>
  );
}


function GraphPanel({ data }) {
  const isDone = data.status === 'DONE';
  const PanelContent = (
    <>
      <div className="graph-id">ID графа изменений: {data.id} </div>
      <div className="graph-stats">
        Создан: {data.dateTime}
      </div>
      <div className="graph-stats">
        Сервисов внесено {data.commitedMicroservices}/{data.associatedMicroservices}
      </div>
      {!isDone && <div className="status-badge">{data.status}</div>}
    </>
  );

  return (
    <div className={`graph-panel-container ${isDone ? '' : 'disabled'}`}>
      {isDone ? (
        <a href={`/change-graph/${data.id}`} className="graph-link">
          {PanelContent}
        </a>
      ) : (
        <div className="graph-link disabled">
          {PanelContent}
        </div>
      )}
    </div>
  );
}

export default ChangeGraphsList;