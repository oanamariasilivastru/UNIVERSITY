// src/components/SimilarContradictingList.jsx
import React, { useState } from 'react';
import PropTypes from 'prop-types';
import Highlighter from 'react-highlight-words';
import { FaStar } from 'react-icons/fa'; // For star ratings
import '../theme/SimilarContradictingList.css';

/**
 * ExpandableText Component:
 * Displays the full text if it is shorter than maxLength.
 * Otherwise, shows a truncated version along with a toggle button.
 */
function ExpandableText({ text, searchWords, maxLength = 300 }) {
  const [expanded, setExpanded] = useState(false);

  if (!text) return null;

  if (text.length <= maxLength) {
    // For short texts, display all text without a toggle button
    return (
      <Highlighter
        highlightClassName="highlight"
        searchWords={searchWords}
        autoEscape={true}
        textToHighlight={text}
      />
    );
  }

  // For long texts, show only a portion if not expanded
  const textToShow = expanded ? text : text.slice(0, maxLength) + '...';

  return (
    <div className="expandable-text">
      <Highlighter
        highlightClassName="highlight"
        searchWords={searchWords}
        autoEscape={true}
        textToHighlight={textToShow}
      />
      <button className="see-more-button" onClick={() => setExpanded(!expanded)}>
        {expanded ? 'See Less' : 'See More'}
      </button>
    </div>
  );
}

ExpandableText.propTypes = {
  text: PropTypes.string.isRequired,
  searchWords: PropTypes.arrayOf(PropTypes.string).isRequired,
  maxLength: PropTypes.number,
};

const SimilarContradictingList = ({ results }) => {
  // Hardcoded data for 3 scenarios
  const data = {
    hurricaneIan: {
      prompt: 'Hurricane Ian Strikes Florida',
      searchWords: ['hurricane', 'ian', 'florida', 'evacuation', 'damage'],
      similarArticles: [
        {
          source: 'The Florida Herald',
          score: 4.7,
          text: `As Hurricane Ian made landfall on Florida’s Gulf Coast, the region experienced catastrophic flooding alongside winds reaching over 150 mph. State officials declared a mandatory evacuation in low-lying areas, urging residents to find safe shelter. Emergency services were stretched thin as rescue operations commenced, with numerous reports of stranded individuals seeking assistance. The local infrastructure suffered severe damage, including collapsed bridges and destroyed homes. Recovery efforts are expected to take months, if not years, as the community rallies together to rebuild and recover from the devastation caused by the hurricane. In the aftermath, discussions have arisen regarding improved building codes and better preparedness for future storms. Local businesses have been hit hard, with many facing prolonged closures due to damage and supply chain disruptions. Volunteer organizations have mobilized to provide food, water, and medical supplies to affected areas, highlighting the resilience and solidarity of the Florida community in the face of natural disasters. Additionally, there is a growing conversation about the role of climate change in exacerbating the intensity of such hurricanes, prompting policymakers to consider more sustainable and proactive measures to mitigate future risks.`,
        },
        {
          source: 'Miami Daily News',
          score: 4.5,
          text: `Local volunteers teamed up with the National Guard to distribute supplies and provide immediate relief to those who lost electricity, clean water, and basic necessities. While power companies struggled to restore service to millions of customers, community centers were transformed into makeshift shelters, offering refuge and support to displaced families. Hospitals operated under emergency protocols to handle an influx of patients with injuries and stress-related conditions. The Miami Daily News also reported on the significant impact on tourism, a major economic driver in the region, with hotels and attractions facing unprecedented cancellations and revenue losses. Environmental concerns have been raised regarding the disposal of debris and the restoration of natural habitats affected by the hurricane’s wrath. Long-term plans include enhancing flood defenses and investing in more resilient infrastructure to better withstand future hurricanes. The collaboration between government agencies, non-profits, and local businesses underscores the comprehensive approach needed to address the multifaceted challenges posed by such natural disasters.`,
        },
        {
          source: 'Sunshine Observer',
          score: 4.3,
          text: `According to preliminary forecasts, the economic toll could climb into tens of billions of dollars, surpassing initial predictions. Infrastructure such as bridges, highways, and power grids took a severe hit, disrupting daily life and hindering recovery efforts. The Sunshine Observer delves into the specifics of the damage, highlighting the destruction of critical transportation links that have isolated certain communities. Efforts to rebuild are hampered by supply shortages and logistical challenges, with construction materials in high demand and limited availability. The article also explores the psychological impact on residents, many of whom are grappling with trauma and loss, and the importance of mental health support in the recovery process. Additionally, there is a focus on the environmental repercussions, including the contamination of waterways and the loss of green spaces, necessitating comprehensive environmental remediation initiatives.`,
        },
        {
          source: 'Gulf Coast Times',
          score: 4.2,
          text: `Even as the winds subsided, the aftermath revealed widespread devastation. Residents reported that certain neighborhoods looked almost unrecognizable, with streets clogged by debris, uprooted trees, and abandoned vehicles. The Gulf Coast Times provides a detailed account of the challenges faced by local authorities in clearing roads and restoring essential services. Community-driven clean-up efforts have been crucial, with neighbors helping neighbors to salvage what they can and rebuild their lives. The article also touches upon the strain on local resources, including limited access to clean water and medical supplies, and the role of neighboring states in providing assistance. Long-term recovery plans are being formulated, focusing on sustainable rebuilding practices that incorporate lessons learned from Hurricane Ian. The resilience of the Gulf Coast community shines through as they navigate the complexities of rebuilding in the wake of such a powerful storm.`,
        },
        {
          source: 'National Weather Journal',
          score: 4.0,
          text: `Long-term effects could be felt for months or even years, as communities slowly rebuild schools, businesses, and residential districts. Environmental scientists also worry about saltwater intrusion into freshwater systems, which could have lasting impacts on agriculture and drinking water supplies. The National Weather Journal discusses the broader climatic patterns that may have influenced Hurricane Ian's intensity and trajectory, examining data on ocean temperatures and atmospheric conditions. The article emphasizes the need for improved predictive models to better anticipate such powerful storms and implement timely evacuation and response strategies. Additionally, it explores the potential for technological advancements in disaster management, including the use of drones for damage assessment and the implementation of smart infrastructure designed to withstand extreme weather events. Collaborative efforts between meteorologists, urban planners, and policymakers are highlighted as essential components in mitigating the effects of future hurricanes.`,
        },
      ],
      contradictingArticles: [
        {
          source: 'Coastal Beacon',
          text: `Contrary to widespread reports of devastation, a few local commentators argued that certain inland regions of Florida were largely unaffected, experiencing only moderate rain and brief power outages. They claimed the media overhyped the situation to attract more viewers and advertisers, suggesting that the true impact of Hurricane Ian has been exaggerated. These commentators pointed to specific areas where infrastructure remained intact and there were minimal disruptions to daily life, emphasizing the resilience of these communities. They also criticized the emergency response, stating that resources were disproportionately allocated to certain regions while neglecting others that did not require extensive aid. Furthermore, they questioned the long-term economic projections, arguing that the tourism sector would rebound quickly without sustained government intervention.`,
        },
        {
          source: 'SocialBuzz TV',
          text: `A handful of social media influencers posted videos showing normal daily life in some neighborhoods, suggesting that evacuation orders might have been excessive. These influencers showcased local cafes, schools, and parks operating as usual, challenging the narrative of widespread chaos and destruction. They argued that the overemphasis on evacuation created unnecessary panic among residents, leading to economic losses for small businesses and disrupting the community fabric. Local authorities responded by highlighting areas that did require evacuation and the reasons behind those decisions, emphasizing the unpredictable nature of hurricanes and the need for caution.`,
        },
        {
          source: 'Economic Insider',
          text: `Some economists insisted that the overall economic hit would be minimal because of Florida’s robust tourism sector. They believe that the state’s disaster-response infrastructure would expedite repairs and restore normal activity within weeks, mitigating long-term financial impacts. Economic Insider analysts pointed out that historical data shows rapid recovery in tourism-driven economies following natural disasters, citing previous hurricanes that had temporarily disrupted but ultimately reinforced the sector’s resilience. They also highlighted investments in infrastructure and emergency preparedness that reduce downtime and facilitate swift recovery.`,
        },
      ],
    },

    volcanoIceland: {
      prompt: 'Volcanic Eruption in Iceland’s Reykjanes Peninsula',
      searchWords: ['volcano', 'iceland', 'eruption', 'lava', 'peninsula'],
      similarArticles: [
        {
          source: 'Reykjavik Post',
          score: 4.6,
          text: `A massive volcanic eruption began along Iceland’s Reykjanes Peninsula, spewing large amounts of lava and ash into the atmosphere. Volcanologists had closely monitored seismic activity for months, and the eruption, while initially confined to a single fissure, quickly expanded in intensity. The article provides an in-depth analysis of the geological factors contributing to the eruption, including tectonic plate movements and magma chamber dynamics, while exploring its immediate environmental impact.`,
        },
        {
          source: 'Nordic Press Agency',
          score: 4.5,
          text: `Emergency evacuation procedures were activated in nearby fishing villages as molten lava advanced toward the coastline. Although the eruption initially seemed contained, the rapid expansion of lava flows posed a significant threat to local infrastructure. The Nordic Press Agency reports on the logistical challenges faced during evacuation and the cooperation between local authorities and disaster response teams.`,
        },
        {
          source: 'Global Geology Weekly',
          score: 4.4,
          text: `Drone footage captured dramatic visuals as rivers of lava flowed through Iceland’s barren landscapes. Global Geology Weekly delves into the eruption’s geological significance, exploring how natural processes contribute both to destruction and the creation of new landforms. The article also features expert interviews discussing long-term implications for the region.`,
        },
        {
          source: 'Travel Iceland Magazine',
          score: 4.3,
          text: `Local businesses in the tourism sector quickly adapted to the eruption by offering guided tours to safe observation points. Travel Iceland Magazine examines the economic impact on local communities while emphasizing sustainable practices to minimize environmental damage due to increased tourism.`,
        },
        {
          source: 'Icelandic Times',
          score: 4.1,
          text: `The government detailed a multi-phase plan to address the eruption’s long-term impacts, including lava diversion techniques and enhanced air quality monitoring systems. The Icelandic Times discusses both the engineering challenges and the proactive measures put in place to safeguard residents and infrastructure.`,
        },
      ],
      contradictingArticles: [
        {
          source: 'Peninsula Dispatch',
          text: `Some local observers challenged initial headlines claiming severe disruption, noting that while dramatic, the eruption was largely confined to remote areas. They suggested that the actual risk to major population centers was minimal and that early evacuation protocols were highly effective.`,
        },
        {
          source: 'Fjords Critic',
          text: `A few government critics argued that forced evacuations were an overreaction given the slow-moving nature of the lava. Fjords Critic features interviews with local business owners who contended that resources could have been better utilized by employing more measured response strategies.`,
        },
        {
          source: 'EcoWatch Europe',
          text: `Environmental skeptics downplayed the immediate ecological damage, suggesting that volcanic landscapes are naturally resilient and that any adverse effects would be short-lived. EcoWatch Europe advocates a balanced perspective on the environmental impact of eruptions.`,
        },
      ],
    },

    earthquakeJapan: {
      prompt: 'Major Earthquake Rocks Tokyo Region - Magnitude 7.3',
      searchWords: ['earthquake', 'tokyo', 'magnitude', 'japan', 'tsunami'],
      similarArticles: [
        {
          source: 'Tokyo Daily',
          score: 4.7,
          text: `A powerful 7.3 magnitude earthquake struck near Tokyo, Japan, causing high-rise buildings to sway and prompting immediate tsunami advisories. Tokyo Daily provides a comprehensive report on the immediate effects, including structural damage and disruptions to the city’s public transportation network.`,
        },
        {
          source: 'Japan News Wire',
          score: 4.5,
          text: `Local media observed brief panic buying at grocery stores and gas stations following the quake. Japan News Wire analyzes the effectiveness of the emergency broadcast system and examines the economic implications of temporary business closures.`,
        },
        {
          source: 'Seismic Review Online',
          score: 4.4,
          text: `Seismologists anticipated further aftershocks and authorities established temporary shelters for affected residents. Seismic Review Online details the scientific efforts to understand the quake's patterns and the logistics of managing large-scale relief operations.`,
        },
        {
          source: 'Kyodo Analysis',
          score: 4.2,
          text: `The Tokyo Governor’s office praised the city’s earthquake-resistant infrastructure. Kyodo Analysis evaluates the performance of safety measures and suggests areas for improvement in urban resilience and emergency protocols.`,
        },
        {
          source: 'Global Finance Journal',
          score: 4.1,
          text: `Economic analysts noted a temporary market dip following the quake, but anticipated a quick recovery. Global Finance Journal examines the broader economic impact and the role of contingency planning in stabilizing financial markets.`,
        },
      ],
      contradictingArticles: [
        {
          source: 'Urban Voices',
          text: `Various social media posts claimed that the quake warnings were exaggerated, with many Tokyo residents reporting normal daily activities despite the tremors. Urban Voices highlights discrepancies between official reports and public sentiment.`,
        },
        {
          source: 'TrainLine Critics',
          text: `Some commentators argued that shutting down train lines was an overreaction, causing unnecessary economic losses and commuter disruptions. TrainLine Critics presents opinions that question the cost-effectiveness of such precautionary measures.`,
        },
        {
          source: 'Local Biz Watch',
          text: `Several business owners noted only minor interruptions and blamed sensational media reports for overstating the quake’s impact. Local Biz Watch offers a perspective that emphasizes the resilience of Tokyo’s commercial sector.`,
        },
      ],
    },
  };

  // Select scenarios based on keys passed in the results prop (e.g. ['hurricaneIan', 'volcanoIceland', 'earthquakeJapan'])
  const selectedDatas = results.map((key) => data[key]).filter(Boolean);

  if (selectedDatas.length === 0) {
    return <p>No valid results found.</p>;
  }

  return (
    <div className="similar-contradicting-list">
      {selectedDatas.map((scenario, idx) => (
        <div key={idx} className="scenario-section">
          {/* Display the scenario prompt (title) */}
          <h2 className="prompt-title">{scenario.prompt}</h2>
          <div className="columns-container">
            {/* Similar Articles Column */}
            <div className="column similar-column">
              <h3>5 Most Similar Articles</h3>
              {scenario.similarArticles.slice(0, 5).map((article, i) => (
                <div key={i} className="article-item similar-article">
                  <p>
                    <strong>{article.source}</strong>
                  </p>
                  <ExpandableText
                    text={article.text}
                    searchWords={scenario.searchWords}
                    maxLength={400}
                  />
                  <div className="score-section">
                    {[...Array(Math.round(article.score))].map((_, k) => (
                      <FaStar key={k} color="gold" />
                    ))}
                    <span className="score-text"> {article.score}</span>
                  </div>
                </div>
              ))}
            </div>

            {/* Contradicting Articles Column */}
            <div className="column contradicting-column">
              <h3>3 Contradicting Articles</h3>
              {scenario.contradictingArticles.slice(0, 3).map((article, j) => (
                <div key={j} className="article-item contradicting-article">
                  <p>
                    <strong>{article.source}</strong>
                  </p>
                  <ExpandableText
                    text={article.text}
                    searchWords={scenario.searchWords}
                    maxLength={400}
                  />
                </div>
              ))}
            </div>
          </div>
        </div>
      ))}
    </div>
  );
};

SimilarContradictingList.propTypes = {
  // E.g., results should be an array like ['hurricaneIan', 'volcanoIceland', 'earthquakeJapan']
  results: PropTypes.arrayOf(PropTypes.string).isRequired,
};

export default SimilarContradictingList;
