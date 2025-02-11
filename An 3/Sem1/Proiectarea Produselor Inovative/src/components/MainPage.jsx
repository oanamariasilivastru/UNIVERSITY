// src/components/MainPage.jsx

import React, { useState, useEffect } from 'react';
import '../theme/MainPage.css';
import Sidebar from './Sidebar';
import InputForm from './InputForm';
import ResultList from './ResultList';
import ToggleButton from './ToggleButton';
import { FiSun, FiMoon, FiLogOut, FiPlus } from 'react-icons/fi';
import { useNavigate } from 'react-router-dom';
import ProfileModal from './ProfileModal';
import SimilarContradictingList from './SimilarContradictingList';
import PropTypes from 'prop-types';

// Fallback results in case of error or no match
const defaultResults = ['hurricaneIan', 'volcanoIceland', 'earthquakeJapan'];

function MainPage({ setIsAuthenticated }) {
  const [inputText, setInputText] = useState('');
  const [results, setResults] = useState([]);
  const [history, setHistory] = useState([]);

  const [sidebarOpen, setSidebarOpen] = useState(true);
  const [isDarkMode, setIsDarkMode] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [profileModalOpen, setProfileModalOpen] = useState(false);
  const [profileData, setProfileData] = useState({
    name: 'ExistingName',
    surname: 'ExistingSurname',
    role: 'journalist',
    image: null,
  });

  const navigate = useNavigate();

  // On mount, read the local history from localStorage
  useEffect(() => {
    const localHistory = localStorage.getItem('localHistory');
    if (localHistory) {
      try {
        setHistory(JSON.parse(localHistory));
      } catch (e) {
        console.error('Error parsing local history:', e);
      }
    }
  }, []);

  // Token is no longer needed but if required for deployment use:
  // const token = localStorage.getItem('token');
  // For this simulation, we assume the user is already authenticated.
  // If you wish to enforce authentication, you can check a token locally.

  // Dark mode toggle: update body class and store choice in localStorage.
  useEffect(() => {
    document.body.classList.toggle('dark-mode', isDarkMode);
  }, [isDarkMode]);

  useEffect(() => {
    const storedDarkMode = localStorage.getItem('isDarkMode');
    if (storedDarkMode) setIsDarkMode(JSON.parse(storedDarkMode));
  }, []);

  useEffect(() => {
    localStorage.setItem('isDarkMode', JSON.stringify(isDarkMode));
  }, [isDarkMode]);

  const handleInputChange = (value) => {
    setInputText(value);
  };

  // Since we are no longer talking to a backend,
  // we simulate fetching history by relying on localStorage only.
  const fetchHistory = () => {
    const localHistory = localStorage.getItem('localHistory');
    if (localHistory) {
      try {
        setHistory(JSON.parse(localHistory));
      } catch (e) {
        console.error('Error parsing local history:', e);
      }
    }
  };

  // Predefined queries mapping (all keys are lower case)
  const predefinedQueriesMap = {
    'impact of hurricane ian on florida': 'hurricaneIan',
    "volcanic eruption in iceland's reykjanes peninsula": 'volcanoIceland',
    'earthquake strikes tokyo - magnitude 7.3': 'earthquakeJapan',
  };

  // Short prompts based on the predefined queries
  const scenarioData = {
    hurricaneIan: {
      prompt: 'impact of hurricane ian on florida',
    },
    volcanoIceland: {
      prompt: "volcanic eruption in iceland's reykjanes peninsula",
    },
    earthquakeJapan: {
      prompt: 'earthquake strikes tokyo - magnitude 7.3',
    },
  };

  // This function simulates a similarity check process, including a delay.
  const checkSimilarity = async () => {
    if (!inputText || inputText.trim() === '') {
      setError('The input text is empty.');
      return;
    }
  
    const normalizedInput = inputText.toLowerCase().trim();
    const matchedKey = predefinedQueriesMap[normalizedInput];

    if (matchedKey) {
      console.log("Recognized phrase. Displaying hardcoded data for:", matchedKey);
      // Set results directly from predefined mapping without any prompt from a backend.
      setResults([matchedKey]);
      setError(null);

      const newHistoryItem = {
        text: inputText,
        date: new Date().toISOString(),
        results: [matchedKey],
        prompt: scenarioData[matchedKey].prompt,
      };

      setHistory((prevHistory) => {
        const updatedHistory = [newHistoryItem, ...prevHistory];
        localStorage.setItem('localHistory', JSON.stringify(updatedHistory));
        return updatedHistory;
      });
      return;
    }

    setLoading(true);
    setError(null);

    // Simulate a backend delay
    try {
      await new Promise((resolve) => setTimeout(resolve, 2000));

      // For unrecognized input, simulate a prediction result
      const simulatedResults = defaultResults;
      setResults(simulatedResults);

      const newHistoryItem = {
        text: inputText,
        date: new Date().toISOString(),
        results: simulatedResults,
        prompt: 'Simulated prompt based on input.',
      };

      setHistory((prevHistory) => {
        const updatedHistory = [newHistoryItem, ...prevHistory];
        localStorage.setItem('localHistory', JSON.stringify(updatedHistory));
        return updatedHistory;
      });
    } catch (error) {
      console.error('Error during similarity check:', error);
      setError(`Error: ${error.message}. Displaying default information.`);
      setResults(defaultResults);
    } finally {
      setLoading(false);
    }
  };

  const toggleSidebar = () => setSidebarOpen(!sidebarOpen);
  const toggleDarkMode = () => setIsDarkMode(!isDarkMode);

  const handleLogout = () => {
    // For local simulation, simply clear any locally stored token and user details
    localStorage.removeItem('token');
    setIsAuthenticated(false);
    navigate('/');
  };

  const handleHistoryItemClick = (historyItem) => {
    setInputText(historyItem.text);
    setResults(historyItem.results);
  };

  const handleProfileClick = () => {
    console.log("Profile button clicked.");
    setProfileModalOpen(true);
  };

  const handleProfileSave = (updatedProfile) => {
    console.log("Updated profile:", updatedProfile);
    setProfileData(updatedProfile);
  };

  // Simulate navigation to a document upload page
  const handleAddDocumentClick = () => {
    navigate('/add-document');
  };

  return (
    <div className={`app-container ${isDarkMode ? 'dark-mode' : ''}`}>
      <Sidebar
        isOpen={sidebarOpen}
        history={history}
        user={{ name: `${profileData.name} ${profileData.surname}`, avatar: profileData.image }}
        onHistoryItemClick={handleHistoryItemClick}
        onProfileClick={handleProfileClick}
      />
      <ToggleButton isOpen={sidebarOpen} toggleSidebar={toggleSidebar} />

      <button
        onClick={toggleDarkMode}
        className="theme-toggle-button"
        aria-label="Toggle Dark Mode"
      >
        {isDarkMode ? <FiSun size={20} /> : <FiMoon size={20} />}
      </button>

      <button onClick={handleLogout} className="logout-button" aria-label="Logout">
        <FiLogOut size={20} />
      </button>

      {/* "Check Document" button navigates to document upload page */}
      <button
        onClick={handleAddDocumentClick}
        className="verify-document-button"
        aria-label="Verifică Document"
      >
        <FiPlus size={20} className="icon" />
        Check document
      </button>
      
      <div className="main-content">
        <h1 className="title">SimQuery</h1>
        <InputForm
          inputText={inputText}
          handleInputChange={handleInputChange}
          checkSimilarity={checkSimilarity}
        />
        {loading && <p>Loading...</p>}
        {error && <p className="error-message">{error}</p>}

        {/* Display results and any additional simulated contradictory articles */}
        <ResultList results={results} />
        <SimilarContradictingList results={results} />
      </div>

      <ProfileModal
        isOpen={profileModalOpen}
        onClose={() => setProfileModalOpen(false)}
        onSave={handleProfileSave}
        initialProfile={profileData}
      />
    </div>
  );
}

MainPage.propTypes = {
  setIsAuthenticated: PropTypes.func.isRequired,
};

export default MainPage;
