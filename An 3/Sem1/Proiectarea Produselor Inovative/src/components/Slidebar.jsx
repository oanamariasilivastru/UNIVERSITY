// src/components/Sidebar.jsx
import React from 'react';
import HistoryItem from './HistoryItem';
import { FiUser, FiEye } from 'react-icons/fi';
import PropTypes from 'prop-types';
import '../theme/Sidebar.css';

const Sidebar = ({ isOpen, user, history, onHistoryItemClick, onProfileClick }) => {
  return (
    <div className={`sidebar ${isOpen ? 'open' : 'closed'}`}>
      {/* User Information Section */}
      <div className="user-info">
        <div className="avatar">
          {user.avatar ? (
            <img src={user.avatar} alt="User Avatar" className="avatar-image" />
          ) : (
            <FiUser size={36} />
          )}
        </div>
        <div className="user-name">{user.name}</div>
      </div>

      {/* View Profile Section */}
      <div
        className="profile-view"
        onClick={onProfileClick}
        style={{ cursor: 'pointer' }}
      >
        <FiEye size={20} />
        <span>View Profile</span>
      </div>

      {/* Search History Section */}
      <div className="history">
        <h2>Search History</h2>
        {history.length === 0 ? (
          <p>No searches performed.</p>
        ) : (
          history.map((item, index) => (
            <HistoryItem
              key={index}
              text={
                Array.isArray(item.texts)
                  ? item.texts.join(', ')
                  : (item.texts || '')
              }
              date={item.date}
              prompt={item.prompt || 'No prompt available'}
              onClick={() => onHistoryItemClick(item)}
            />
          ))
        )}
      </div>
    </div>
  );
};

Sidebar.propTypes = {
  isOpen: PropTypes.bool.isRequired,
  user: PropTypes.shape({
    name: PropTypes.string.isRequired,
    avatar: PropTypes.string,
  }).isRequired,
  history: PropTypes.arrayOf(
    PropTypes.shape({
      texts: PropTypes.oneOfType([
        PropTypes.arrayOf(PropTypes.string),
        PropTypes.string,
      ]),
      date: PropTypes.string.isRequired,
      results: PropTypes.array,
      prompt: PropTypes.string,
    })
  ).isRequired,
  onHistoryItemClick: PropTypes.func.isRequired,
  onProfileClick: PropTypes.func.isRequired,
};

export default Sidebar;
