// src/components/ResultList.jsx
import React from 'react';
import PropTypes from 'prop-types';
import ResultItem from './ResultItem';

const ResultList = ({ results }) => {
  if (!results || results.length === 0) {
    return null;
  }

  // Filter results that have text defined
  const filteredResults = results.filter((result) => result.text);

  if (filteredResults.length === 0) {
    return null; // Do not render anything if no valid results exist
  }

  return (
    <div className="results-container">
      <ul className="results-list">
        {filteredResults.map((result, index) => (
          <ResultItem
            key={index}
            index={index}
            sentence={result.text}
            score={result.score || 0}
            searchWords={result.searchWords || []}
          />
        ))}
      </ul>
    </div>
  );
};

ResultList.propTypes = {
  results: PropTypes.arrayOf(
    PropTypes.shape({
      text: PropTypes.string.isRequired,
      score: PropTypes.number.isRequired,
      searchWords: PropTypes.arrayOf(PropTypes.string),
    })
  ).isRequired,
};

export default ResultList;
