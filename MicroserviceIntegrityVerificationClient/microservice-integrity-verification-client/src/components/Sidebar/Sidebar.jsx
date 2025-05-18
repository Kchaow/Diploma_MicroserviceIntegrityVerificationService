import React from 'react';
import './Sidebar.css';

const Sidebar = () => {
  return (
    <div className="sidebar">
      <ul className="sidebar-menu">
        <li><a href="/">Главная</a></li>
        <li><a href="/verification-graph">Общий граф</a></li>
        <li><a href="/change-graph">Графы изменений</a></li>
      </ul>
    </div>
  );
};

export default Sidebar;
