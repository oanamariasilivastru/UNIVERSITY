// src/components/HistoryItem.jsx

import React from 'react';
import { FiClock } from 'react-icons/fi';
import PropTypes from 'prop-types';

const HistoryItem = ({ text = '', date, prompt = '', onClick }) => {
  let formattedDate;

  try {
    // Use Intl.DateTimeFormat to format the date locally.
    const dateObj = new Date(date);
    const formatter = new Intl.DateTimeFormat('en-GB', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
      hour12: false,
    });
    formattedDate = formatter.format(dateObj);
  } catch (error) {
    console.error('Error formatting date:', error);
    formattedDate = 'Invalid Date';
  }

  // Split the text on hyphen ("-") if available into title and body.
  const hyphenIndex = text.indexOf('-');
  let title = '';
  let body = '';

  if (hyphenIndex !== -1) {
    title = text.substring(0, hyphenIndex).trim();
    body = text.substring(hyphenIndex + 1).trim();
  } else {
    body = text;
  }

  return (
    <div className="history-item" onClick={onClick} style={{ cursor: 'pointer' }}>
      <p className="history-text">
        {title && <strong>{title} - </strong>}
        <span className="history-body">{body}</span>
      </p>

      {/* Display prompt if provided */}
      {prompt && (
        <p className="history-prompt">
          <em>{prompt}</em>
        </p>
      )}

      <div className="history-date">
        <FiClock className="mr-1" />
        <span>{formattedDate}</span>
      </div>
    </div>
  );
};

HistoryItem.propTypes = {
  text: PropTypes.string,
  date: PropTypes.string.isRequired,
  prompt: PropTypes.string,
  onClick: PropTypes.func.isRequired,
};

export default HistoryItem;
