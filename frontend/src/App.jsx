
import { useState, useEffect } from 'react'
import { BrowserRouter, Routes, Route } from 'react-router-dom'
import './App.css'
import Auth from './components/Auth';
import ResetPassword from './components/ResetPassword';
import { apiRequest } from './api';
const careerPaths = [
  {
    id: 1,
    title: 'Data Analyst',
    skills: ['Python', 'SQL', 'Excel', 'Power BI', 'Statistics']
  },
  {
    id: 2,
    title: 'Frontend Developer',
    skills: ['HTML', 'CSS', 'JavaScript', 'React', 'Git']
  },
  {
    id: 3,
    title: 'Machine Learning Engineer',
    skills: ['Python', 'Machine Learning', 'Statistics', 'SQL', 'Deep Learning']
  },
  {
    id: 4,
    title: 'Data Scientist',
    skills: ['Python', 'SQL', 'Statistics', 'Machine Learning', 'Data Visualization']
  }
]
function DashboardApp() {
  const [isLoggedIn, setIsLoggedIn] = useState(
    Boolean(localStorage.getItem('token'))
);
  const [activeTab, setActiveTab] = useState('Dashboard')
const [skills, setSkills] = useState([])
const [newSkill, setNewSkill] = useState('')
const [category, setCategory] = useState('Technical')
const [careerProgress, setCareerProgress] = useState({})
const [selectedCareer, setSelectedCareer] = useState(() => {
  const savedCareer = localStorage.getItem('selectedCareer')
  return savedCareer ? JSON.parse(savedCareer) : null
})
const [roadmapItems, setRoadmapItems] = useState([])
const [completedSkills, setCompletedSkills] = useState([])
const [customRoadmapItems, setCustomRoadmapItems] = useState([])
const [roadmapLoading, setRoadmapLoading] = useState(false)
const [newRoadmapItem, setNewRoadmapItem] = useState('')
useEffect(() => {
  if (!selectedCareer) {
    setRoadmapItems([])
    setCompletedSkills([])
    setCustomRoadmapItems([])
    return
  }

  const loadRoadmap = async () => {
    setRoadmapLoading(true)

    try {
      const [roadmapData, customData] = await Promise.all([
        apiRequest(`/api/roadmap/${selectedCareer.id}`),
        apiRequest(`/api/custom-roadmap/${selectedCareer.id}`)
      ])

      setRoadmapItems(roadmapData)
      setCustomRoadmapItems(customData.map(item => item.topicName))

      setCompletedSkills(
        [...roadmapData, ...customData]
          .filter(item => item.isCompleted)
          .map(item => item.topicName)
      )
    } catch (error) {
      alert(error.message || 'Could not load your roadmap.')
    } finally {
      setRoadmapLoading(false)
    }
  }

  loadRoadmap()
}, [selectedCareer])

const matchedSkills = selectedCareer
  ? selectedCareer.skills.filter(skill =>
      skills.some(userSkill =>
        userSkill.skillName.toLowerCase() === skill.toLowerCase()
      )
    )
  : []

const matchPercentage = selectedCareer
  ? Math.round(
      (matchedSkills.length / selectedCareer.skills.length) * 100
    )
  : 0
  const missingSkills = selectedCareer
  ? selectedCareer.skills.filter(skill =>
      !matchedSkills.some(
        matched => matched.toLowerCase() === skill.toLowerCase()
      )
    )
  : []

const allRoadmapItems = [
  ...roadmapItems.map(item => item.topicName),
  ...customRoadmapItems
]


useEffect(() => {
  if (!isLoggedIn || activeTab !== 'My Progress') return

  const loadCareerProgress = async () => {
    try {
      const results = await Promise.all(
        careerPaths.map(async career => {
          const progress = await apiRequest(
            `/api/progress/${career.id}`
          )
          return [career.id, progress]
        })
      )

      setCareerProgress(Object.fromEntries(results))
    } catch (error) {
      console.error('Could not load career progress:', error)
    }
  }

  loadCareerProgress()
}, [isLoggedIn, activeTab])

useEffect(() => {
  if (!isLoggedIn) {
    setSkills([])
    return
  }

  const loadSkills = async () => {
    try {
      const data = await apiRequest('/api/skills')
      setSkills(data)
    } catch (error) {
      console.error('Could not load skills:', error)
    }
  }

  loadSkills()
}, [isLoggedIn])

const handleAddRoadmapItem = async () => {
  const trimmedItem = newRoadmapItem.trim()

  if (!trimmedItem || !selectedCareer) return

  const alreadyExists = allRoadmapItems.some(
    item => item.toLowerCase() === trimmedItem.toLowerCase()
  )

  if (alreadyExists) {
    alert('This topic already exists!')
    return
  }

  try {
    const savedItem = await apiRequest(
      `/api/custom-roadmap/${selectedCareer.id}`,
      {
        method: 'POST',
        body: JSON.stringify({ topicName: trimmedItem })
      }
    )

    setCustomRoadmapItems(previous => [
      ...previous,
      savedItem.topicName
    ])

    setNewRoadmapItem('')
  } catch (error) {
    alert(error.message || 'Could not add roadmap item.')
  }
}

const handleRemoveSkill = async (skillId) => {
  try {
    await apiRequest(`/api/skills/${skillId}`, {
      method: 'DELETE'
    })

    setSkills(previous =>
      previous.filter(skill => skill.id !== skillId)
    )
  } catch (error) {
    alert(error.message || 'Could not delete skill.')
  }
}
const toggleRoadmapSkill = async (skill) => {
  const item = [...roadmapItems].find(
    item => item.topicName === skill
  )

  if (!item) {
    const customItem = await apiRequest(
      `/api/custom-roadmap/${selectedCareer.id}`
    )

    const found = customItem.find(
      entry => entry.topicName === skill
    )

    if (!found) return

    try {
      await apiRequest(
        `/api/custom-roadmap/${found.id}/completion`,
        {
          method: 'PUT',
          body: JSON.stringify({ completed: !found.isCompleted })
        }
      )

      setCompletedSkills(previous =>
        found.isCompleted
          ? previous.filter(value => value !== skill)
          : [...previous, skill]
      )
    } catch (error) {
      alert(error.message)
    }

    return
  }

  try {
    await apiRequest(`/api/roadmap/${item.id}/completion`, {
      method: 'PUT',
      body: JSON.stringify({ completed: !item.isCompleted })
    })

    setRoadmapItems(previous =>
      previous.map(entry =>
        entry.id === item.id
          ? { ...entry, isCompleted: !entry.isCompleted }
          : entry
      )
    )

    setCompletedSkills(previous =>
      item.isCompleted
        ? previous.filter(value => value !== skill)
        : [...previous, skill]
    )
  } catch (error) {
    alert(error.message || 'Could not update progress.')
  }
}
const handleAddSkill = async () => {
  const trimmedSkill = newSkill.trim()

  if (!trimmedSkill) return

  const alreadyExists = skills.some(
    skill =>
      skill.skillName.toLowerCase() === trimmedSkill.toLowerCase()
  )

  if (alreadyExists) {
    alert('This skill already exists!')
    return
  }

  try {
    const savedSkill = await apiRequest('/api/skills', {
      method: 'POST',
      body: JSON.stringify({
        skillName: trimmedSkill,
        category: category
      })
    })

    setSkills(previous => [...previous, savedSkill])
    setNewSkill('')
  } catch (error) {
    alert(error.message)
  }
}





const menuItems = [
  'Dashboard',
  'My Skills',
  'Career Paths',
  'Skill Analysis',
  'My Roadmap',
  'My Progress'
]

  if (!isLoggedIn) {
    return <Auth onLogin={() => setIsLoggedIn(true)} />;
  }

  return (
    <div className="app-container">

      <aside className="sidebar">
        <div className="brand">
          <div className="brand-icon">S</div>
          <span>SkillPath</span>
        </div>

        <p className="menu-heading">MAIN MENU</p>

        <nav>
          {menuItems.map((item) => (
            <button
              key={item}
              className={`nav-item ${activeTab === item ? 'active' : ''}`}
              onClick={() => setActiveTab(item)}
            >
              {item === 'Dashboard' ? '▦' :
               item === 'My Skills' ? '✧' :
               item === 'Career Paths' ? '⌘' :
               item === 'Skill Analysis' ? '◈' :
               item === 'My Roadmap' ? '☷' : '◷'}
              <span>{item}</span>
            </button>
          ))}
          <button
  className="nav-item"
  onClick={() => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setIsLoggedIn(false);
  }}
>
 ↪
    <span>Logout</span>
</button>
        </nav>

        <div className="sidebar-bottom">
          <div className="profile-avatar">D</div>
          <div>
            <strong>Student</strong>
            <p>My Account</p>
          </div>
        </div>
      </aside>

      <main className="main-content">

        <header className="topbar">
          <div>
            <p className="breadcrumb">Pages / {activeTab}</p>
            <h3>{activeTab}</h3>
          </div>

          <div className="topbar-right">
            <span className="notification">♧</span>
            <div className="user-avatar">D</div>
          </div>
        </header>


  {activeTab === 'My Roadmap' ? (
  <section className="roadmap-page">
    <div className="skills-header">
      <h1>My Learning Roadmap</h1>
      <p>Follow your personalised learning plan to reach your career goal.</p>
    </div>
  {!selectedCareer ? (
      <div className="panel no-career-panel">
        <h2>No Career Selected Yet</h2>
        <p>Select a career first to generate your learning roadmap.</p>
        <button
          className="primary-btn"
          onClick={() => setActiveTab('Career Paths')}
        >
          Explore Career Paths →
        </button>
      </div>
    ) : (
      <div className="panel roadmap-panel">
        <div className="panel-heading">
          <div>
            <h2>{selectedCareer.title}</h2>
            <p>Your skills to learn, in order</p>
          </div>
          <span className="status-badge">Personalised Plan</span>
        </div>

        <div className="roadmap-progress">
          <div className="progress-heading">
            <span>Learning Progress</span>
            <strong>
              {allRoadmapItems.length === 0
                ? 100
                : Math.round(
                    (completedSkills.filter(skill =>
                      allRoadmapItems.includes(skill)
                    ).length / allRoadmapItems.length) * 100
                  )}%
            </strong>
          </div>
          <div className="progress-bar">
            <div
              style={{
                width: `${
                  allRoadmapItems.length === 0
                    ? 100
                    : (completedSkills.filter(skill =>
                        allRoadmapItems.includes(skill)
                      ).length / allRoadmapItems.length) * 100
                }%`
              }}
            ></div>
          </div>
        </div>

        <div className="add-roadmap-item" style={{ display: 'flex', gap: '10px', margin: '22px 0' }}>
          <input
            type="text"
            placeholder="Add your own learning topic..."
            value={newRoadmapItem}
            onChange={(e) => setNewRoadmapItem(e.target.value)}
            style={{ flex: 1, padding: '12px', border: '1px solid #e2e3ed', borderRadius: '8px' }}
          />
           <button className="primary-btn" onClick={handleAddRoadmapItem}>
            + Add to Roadmap
          </button>
        </div>

        {allRoadmapItems.length === 0 ? (
          <p className="roadmap-empty">
            Great! You already have all the required skills. Add a custom topic above if you want to learn something extra.
          </p>
        ) : (
          <div className="roadmap-list">
            {allRoadmapItems.map((skill, index) => (
              <div className="roadmap-item" key={`${selectedCareer.id}-${skill}`}>
                <div className="roadmap-number">
                  {completedSkills.includes(skill) ? '✓' : index + 1}
                </div>

                <div className="roadmap-skill-info">
                  <strong>{skill}</strong>
                  <p>{customRoadmapItems.includes(skill) ? 'Custom Learning Step' : `Learning Step ${index + 1}`}</p>
                </div>

              <button
                  className={
                    completedSkills.includes(skill)
                      ? 'roadmap-done'
                      : 'roadmap-action'
                  }
                  onClick={() => toggleRoadmapSkill(skill)}
                >
                  {completedSkills.includes(skill)
                    ? 'Completed ✓'
                    : 'Mark Complete'}
                </button>
              </div>
            ))}
          </div>
        )}
      </div>
    )}
   </section>
) : activeTab === 'My Progress' ? (
  <section className="analysis-page">
    <div className="skills-header">
      <h1>My Learning Progress</h1>
      <p>Track your learning progress across all your career roadmaps.</p>
    </div>

    <div className="career-cards">
      {careerPaths.map((career) => {
        const progress = careerProgress[career.id]

        const completedCount = progress?.completedTopics ?? 0
        const totalTopics = progress?.totalTopics ?? 0
        const percentage = progress?.progressPercentage ?? 0

        return (
          <div className="panel career-option" key={career.id}>
            <h2>{career.title}</h2>

            <div className="progress-heading">
              <span>Learning Progress</span>
              <strong>{percentage}%</strong>
            </div>

            <div className="progress-bar">
              <div style={{ width: `${percentage}%` }}></div>
            </div>

            <p>{completedCount} completed out of {totalTopics} topics</p>

            <button
              className="secondary-btn"
              onClick={() => {
                setSelectedCareer(career)
                localStorage.setItem('selectedCareer', JSON.stringify(career))
                setActiveTab('My Roadmap')
              }}
            >
              View Roadmap →
            </button>
          </div>
        )
      })}
    </div>

    </section>
) : activeTab === 'Skill Analysis' ? (
  <section className="analysis-page">
    <div className="skills-header">
      <h1>Skill Gap Analysis</h1>
      <p>Compare your current skills with your career requirements.</p>
    </div>

    {!selectedCareer ? (
      <div className="panel no-career-panel">
        <h2>No Career Selected Yet</h2>
        <p>Please select a career path first to analyse your skills.</p>
        <button
          className="primary-btn"
          onClick={() => setActiveTab('Career Paths')}
        >
          Explore Career Paths →
        </button>
      </div>
    ) : (
      <>
        <div className="panel analysis-summary">
          <p>YOUR SELECTED CAREER</p>
          <h2>{selectedCareer.title}</h2>

          <div className="match-heading">
            <span>Skill Match</span>
            <strong>{matchPercentage}%</strong>
          </div>

          <div className="progress-bar">
            <div style={{ width: `${matchPercentage}%` }}></div>
          </div>

          <div className="analysis-stats">
            <div>
              <h2>{matchedSkills.length}</h2>
              <p>Matched Skills</p>
            </div>
            <div>
              <h2>{missingSkills.length}</h2>
              <p>Skills to Learn</p>
            </div>
            <div>
              <h2>{selectedCareer.skills.length}</h2>
              <p>Total Required</p>
            </div>
          </div>
        </div>
        <div className="analysis-columns">
          <div className="panel">
            <h2>Skills You Have</h2>
            <p>These skills match your career goal.</p>

             {matchedSkills.length === 0 ? (
              <p>No matching skills yet.</p>
            ) : (
              matchedSkills.map(skill => (
                <div className="analysis-skill matched" key={skill}>
                  <span>{skill}</span>
                  <strong>✓ Matched</strong>
                </div>
              ))
            )}
          </div>

          <div className="panel">
            <h2>Skills You Need to Learn</h2>
            <p>These skills are required for your career.</p>

            {missingSkills.length === 0 ? (
              <p>You have all the required skills!</p>
            ) : (
              missingSkills.map(skill => (
                <div className="analysis-skill missing" key={skill}>
                  <span>{skill}</span>
                  <strong>+ Learn</strong>
                </div>
              ))
            )}
          </div>
        </div>
      </>
    )}
  </section>
) : activeTab === 'Career Paths' ? (
  <section className="career-page">
    <div className="skills-header">
      <h1>Explore Career Paths</h1>
      <p>Discover careers and the skills required to achieve your goals.</p>
    </div>

    <div className="career-cards">
      {careerPaths.map((career) => (
        <div className="panel career-option" key={career.id}>
          <span className="career-category">{career.category}</span>
          <h2>{career.title}</h2>
          <p>{career.description}</p>

          <div className="required-skills">
            <strong>Required Skills</strong>
            <div>
              {career.skills.map((skill) => (
                <span className="skill-tag" key={skill}>{skill}</span>
              ))}
            </div>
          </div>

          <div className="career-card-bottom">
            <span>{career.level}</span>
            <button
              className="primary-btn"
              onClick={() => {
  setSelectedCareer(career)
  localStorage.setItem('selectedCareer', JSON.stringify(career))
}}
            >
              Select Career →
            </button>
          </div>
        </div>
      ))}
    </div>

    {selectedCareer && (
      <div className="selected-career-message">
        <strong>Selected Career: {selectedCareer.title}</strong>
        <p>This is your current career choice.</p>
      </div>
    )}
  </section>
) : activeTab === 'My Skills' ? (
  <section className="skills-page">
    <div className="skills-header">
      <div>
        <h1>My Skills</h1>
        <p>Manage your existing skills and build your profile.</p>
      </div>
    </div>

    <div className="panel add-skill-panel">
      <h2>Add a New Skill</h2>
      <p>Enter a skill you already know.</p>

      <div className="skill-form">
        <input
          type="text"
          placeholder="Enter skill name..."
          value={newSkill}
          onChange={(e) => setNewSkill(e.target.value)}
        />

        <select
          value={category}
          onChange={(e) => setCategory(e.target.value)}
        >
          <option>Programming</option>
          <option>Web Development</option>
          <option>Database</option>
          <option>Data Science</option>
          <option>Tools</option>
          <option>Other</option>
        </select>

        <button className="primary-btn" onClick={handleAddSkill}>
          + Add Skill
        </button>
      </div>
    </div>

    <div className="panel skills-list-panel">
      <div className="panel-heading">
        <div>
          <h2>Your Skills</h2>
          <p>{skills.length} skills added to your profile</p>
        </div>
      </div>

      <div className="skills-list">
        {skills.map((skill) => (
          <div className="skill-row" key={skill.id}>
            <div>
              <strong>{skill.skillName}</strong>
              <p>{skill.category}</p>
            </div>

            <button
              className="remove-skill"
              onClick={() => handleRemoveSkill(skill.id)}
            >
              Remove
            </button>
          </div>
        ))}
      </div>
    </div>
  </section>
) : (
  <>

        <section className="welcome-section">
          <div>
            <p className="eyebrow">YOUR CAREER JOURNEY STARTS HERE</p>
            <h1>Welcome back, Student! 👋</h1>
            <p className="welcome-text">
              Discover your skill gaps and build a roadmap
              towards your dream career.
            </p>
            <button
              className="primary-btn"
              onClick={() => setActiveTab('Skill Analysis')}
            >
              Analyse My Skills →
            </button>
          </div>

          <div className="welcome-illustration">
            <div className="illustration-icon">✦</div>
            <h2>Grow Your Skills</h2>
            <p>Build your future, one skill at a time.</p>
          </div>
        </section>

        <section className="stats-grid">
          <div className="stat-card">
            <div className="stat-icon purple">✧</div>
            <p>Total Skills</p>
            <h2>08</h2>
            <span>Skills in your profile</span>
          </div>

          <div className="stat-card">
            <div className="stat-icon blue">◎</div>
            <p>Skills Matched</p>
            <h2>05</h2>
            <span>Matching your career goal</span>
          </div>

          <div className="stat-card">
            <div className="stat-icon orange">◈</div>
            <p>Skills to Learn</p>
            <h2>06</h2>
            <span>Areas to improve</span>
          </div>

<div className="stat-card">
            <div className="stat-icon green">✓</div>
            <p>Completion</p>
            <h2>45%</h2>
            <span>Your learning progress</span>
          </div>
        </section>

        <section className="content-grid">

          <div className="panel career-panel">
            <div className="panel-heading">
              <div>
                <h2>Your Career Goal</h2>
                <p>Your selected career path</p>
              </div>
              <span className="status-badge">Active</span>
            </div>

            <div className="career-icon">📊</div>
            <h3>Data Analyst</h3>
            <p className="career-description">
              Build expertise in data analysis, statistics,
              SQL and visualization.
            </p>

            <div className="progress-heading">
              <span>Overall Progress</span>
              <strong>45%</strong>
            </div>

            <div className="progress-bar">
              <div style={{ width: '45%' }}></div>
            </div>

            <button
              className="secondary-btn"
              onClick={() => setActiveTab('My Roadmap')}
            >
              View Roadmap →
            </button>
          </div>

          <div className="panel activity-panel">
            <div className="panel-heading">
              <div>
                <h2>Learning Overview</h2>
                <p>Your skill development status</p>
              </div>
            </div>
              <div className="activity-item">
              <div className="activity-dot completed"></div>
              <div>
                <strong>Completed Skills</strong>
                <p>Python, Excel, HTML</p>
              </div>
              <span>3</span>
            </div>

            <div className="activity-item">
              <div className="activity-dot ongoing"></div>
              <div>
                <strong>Currently Learning</strong>
                <p>SQL, Statistics</p>
              </div>
              <span>2</span>
            </div>

            <div className="activity-item">
              <div className="activity-dot pending"></div>
              <div>
                <strong>Yet to Start</strong>
                <p>Power BI, Tableau</p>
              </div>
              <span>3</span>
            </div>
          </div>

        </section>

        <footer>
          <p>© 2026 SkillPath. Plan your skills. Shape your future.</p>
        </footer>
    </>
        )}
      </main>
    </div>
  )
}
function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<DashboardApp />} />
        <Route path="/reset-password" element={<ResetPassword />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App