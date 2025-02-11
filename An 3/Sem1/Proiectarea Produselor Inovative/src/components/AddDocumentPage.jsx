// src/components/AddDocumentPage.jsx

import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { FiArrowLeft, FiUpload } from "react-icons/fi";
import { FaStar } from "react-icons/fa"; // For displaying the confidence score
import "../theme/AddDocumentPage.css";

function AddDocumentPage() {
  const [file, setFile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [sources, setSources] = useState([]);
  const [error, setError] = useState(null);
  const [confidenceScore, setConfidenceScore] = useState(null);

  const navigate = useNavigate();

  const handleFileChange = (e) => {
    setFile(e.target.files[0]);
    setSources([]);
    setConfidenceScore(null);
    setError(null);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!file) {
      setError("Please upload a file.");
      return;
    }

    setLoading(true);
    setError(null);

    // Simulate processing locally
    try {
      await new Promise((resolve) => setTimeout(resolve, 2000)); // Simulate delay

      const score = Math.floor(Math.random() * 31) + 70; // Random score between 70-100
      setConfidenceScore(score);

      // Hardcoded mock sources with full paragraphs (no ellipsis)
      const hardcodedSources = [
        {
          title: "Source 1: The New York Times",
          location: "Page 2, Line 15",
          paragraph: `As the sun set over the vast agricultural plains of the Midwest, experts began warning that climate change was having a profound impact on crop yields and soil quality. The New York Times investigates the cascading effects of prolonged droughts, increased frequency of extreme weather events, and shifting weather patterns, which together are diminishing agricultural productivity. Local farmers are forced to adapt through new water conservation strategies and investing in drought-resistant crops, although many remain uncertain about the long-term viability of traditional farming methods. The article features in-depth interviews with climate scientists and agricultural economists, who discuss the urgent need for comprehensive policy reforms to support rural communities and ensure food security in the face of environmental upheaval.`,
        },
        {
          title: "Source 2: BBC News",
          location: "Page 5, Line 3",
          paragraph: `In a detailed report, BBC News explores how coastal communities are experiencing the direct consequences of rising sea levels and intensifying storms. The investigation highlights personal stories of residents who have seen their homes and livelihoods affected by unprecedented weather-related events. Experts are calling for immediate governmental intervention to invest in flood defenses and modernized infrastructure to safeguard critical areas. Beyond the human element, the report examines the economic implications for small businesses, the steep insurance premiums now demanded in high-risk areas, and the long-term sustainability of urban planning in vulnerable coastal zones.`,
        },
        {
          title: "Source 3: CNN",
          location: "Page 3, Line 8",
          paragraph: `CNN provides an eye-opening narrative of communities in the path of climate-induced disasters. Following Hurricane Franklin, which ravaged parts of New Orleans, the article documents the struggles of thousands who lost nearly everything. Interviews with displaced residents reveal the deep personal and financial toll of the disaster, while emergency services and local charities rally to provide temporary shelter and support. The piece also delves into broader governmental shortcomings and discusses potential strategies to improve disaster response and resiliency planning, ensuring that future communities are better prepared to face such challenges head-on.`,
        },
      ];

      setSources(hardcodedSources);
    } catch (err) {
      setError("An error occurred while processing the document.");
    } finally {
      setLoading(false);
    }
  };

  const handleBack = () => {
    navigate(-1);
  };

  return (
    <div className="add-document-container">
      <button onClick={handleBack} className="back-button" aria-label="Back">
        <FiArrowLeft size={24} /> Back
      </button>
      <form onSubmit={handleSubmit} className="add-document-form">
        <div className="form-group">
          <label htmlFor="document">Select a document (PDF, DOCX):</label>
          <input
            type="file"
            id="document"
            accept=".pdf,.doc,.docx"
            onChange={handleFileChange}
            required
          />
        </div>

        <button type="submit" className="submit-button" disabled={loading}>
          <FiUpload size={20} /> Verify
        </button>
      </form>

      {loading && <div className="loading-indicator">Processing document...</div>}

      {error && <p className="error-message">{error}</p>}

      {confidenceScore !== null && (
        <div className="confidence-score">
          <h3>Confidence Score</h3>
          <div className="score-display">
            {[...Array(5)].map((_, index) => (
              <FaStar
                key={index}
                className={index < Math.round(confidenceScore / 20) ? "star filled" : "star"}
              />
            ))}
            <span>{confidenceScore}%</span>
          </div>
        </div>
      )}

      {sources.length > 0 && (
        <div className="sources-container">
          <h3>Detected Sources</h3>
          <ul>
            {sources.map((source, index) => (
              <li key={index}>
                <strong>{source.title}</strong> - {source.location}
                <p>{source.paragraph}</p>
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
}

export default AddDocumentPage;
