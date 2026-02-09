import DashboardLayout from '../layouts/DashboardLayout';
import { Calendar, FileText, Bell, Activity, Clock, CreditCard } from 'lucide-react';
import { motion } from 'framer-motion';

const PatientDashboard = () => {
  return (
    <DashboardLayout>
      <div className="patient-dashboard">
        <header className="dashboard-header">
          <h1>Bonjour, Bienvenue dans votre Espace Santé</h1>
          <p>Retrouvez ici vos rendez-vous, vos bilans médicaux et vos notifications.</p>
        </header>

        <section className="dashboard-grid">
          {/* Main Card: Next Appointment */}
          <div className="card next-appointment-card">
            <div className="card-inner">
              <div className="icon-box">
                <Calendar size={32} />
              </div>
              <div className="appointment-info">
                <h3>Prochain Rendez-vous</h3>
                <p className="primary-text">Mardi 12 Février à 14:30</p>
                <p className="secondary-text">Dr. Sarah Martin - Cardiologie</p>
              </div>
              <button className="btn-accent">Gérer</button>
            </div>
          </div>

          {/* Quick Actions */}
          <div className="quick-actions">
            <motion.div whileHover={{ scale: 1.02 }} className="action-card">
              <Clock size={24} />
              <span>Historique</span>
            </motion.div>
            <motion.div whileHover={{ scale: 1.02 }} className="action-card">
              <FileText size={24} />
              <span>Documents</span>
            </motion.div>
            <motion.div whileHover={{ scale: 1.02 }} className="action-card">
              <CreditCard size={24} />
              <span>Facturation</span>
            </motion.div>
          </div>

          {/* Detailed View: Appointments & Records */}
          <div className="details-section">
            <div className="data-card">
              <h2>Mes derniers bilans</h2>
              <div className="record-list">
                <div className="record-item">
                  <Activity size={20} />
                  <div>
                    <p className="record-name">Analyse de sang</p>
                    <span className="record-date">05 Jan 2024</span>
                  </div>
                  <button className="btn-small">Voir</button>
                </div>
                <div className="record-item">
                  <Activity size={20} />
                  <div>
                    <p className="record-name">Radio Rachis Cervical</p>
                    <span className="record-date">22 Dec 2023</span>
                  </div>
                  <button className="btn-small">Voir</button>
                </div>
              </div>
            </div>

            <div className="notifications-panel">
              <div className="data-card">
                <h2>Notifications</h2>
                <div className="notification-list">
                  <div className="notif-item">
                    <Bell size={18} className="notif-icon" />
                    <p>Votre rendez-vous avec le Dr. Martin a été confirmé.</p>
                  </div>
                  <div className="notif-item">
                    <Bell size={18} className="notif-icon" />
                    <p>Nouveau document disponible : Compte rendu radiologie.</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </section>
      </div>

      <style>{`
        .patient-dashboard {
          max-width: 1200px;
          margin: 0 auto;
        }

        .dashboard-header {
          margin-bottom: 2.5rem;
          text-align: center;
        }

        .dashboard-header h1 {
          font-size: 2.2rem;
          background: linear-gradient(to right, var(--foreground), var(--primary));
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          margin-bottom: 0.5rem;
        }

        .dashboard-grid {
          display: flex;
          flex-direction: column;
          gap: 2rem;
        }

        /* Next Appointment Card */
        .next-appointment-card {
          background: linear-gradient(135deg, var(--primary), #0052cc);
          color: white;
          padding: 2.5rem;
          border-radius: var(--radius-xl);
          box-shadow: 0 20px 30px -10px rgba(0, 102, 255, 0.3);
        }

        .card-inner {
          display: flex;
          align-items: center;
          gap: 2rem;
        }

        .icon-box {
          background: rgba(255, 255, 255, 0.2);
          padding: 1.5rem;
          border-radius: var(--radius-lg);
          backdrop-filter: blur(10px);
        }

        .appointment-info {
          flex: 1;
        }

        .appointment-info h3 {
          font-size: 0.9rem;
          text-transform: uppercase;
          letter-spacing: 1px;
          opacity: 0.8;
          margin-bottom: 0.5rem;
        }

        .primary-text {
          font-size: 1.8rem;
          font-weight: 700;
          margin-bottom: 0.25rem;
        }

        .secondary-text {
          font-size: 1.1rem;
          opacity: 0.9;
        }

        .btn-accent {
          background: white;
          color: var(--primary);
          padding: 0.75rem 1.5rem;
          border-radius: var(--radius-md);
          font-weight: 700;
        }

        /* Quick Actions */
        .quick-actions {
          display: grid;
          grid-template-columns: 1fr 1fr;
          gap: 1.5rem;
        }

        .action-card {
          background: white;
          padding: 2rem;
          border-radius: var(--radius-lg);
          border: 1px solid var(--border);
          display: flex;
          flex-direction: column;
          align-items: center;
          gap: 1rem;
          color: var(--primary);
          cursor: pointer;
        }

        .action-card span {
          color: var(--foreground);
          font-weight: 600;
          font-size: 1.1rem;
        }

        /* Details Section */
        .details-section {
          display: grid;
          grid-template-columns: 1.5fr 1fr;
          gap: 2rem;
        }

        .data-card {
          background: white;
          padding: 2rem;
          border-radius: var(--radius-lg);
          border: 1px solid var(--border);
          height: 100%;
        }

        .data-card h2 {
          font-size: 1.25rem;
          margin-bottom: 1.5rem;
        }

        .record-list, .notification-list {
          display: flex;
          flex-direction: column;
          gap: 1.25rem;
        }

        .record-item {
          display: flex;
          align-items: center;
          gap: 1.25rem;
          padding: 1rem;
          border-radius: var(--radius-md);
          background: #f8fafc;
          border: 1px solid transparent;
          transition: border 0.3s;
        }

        .record-name { font-weight: 600; font-size: 0.95rem; }
        .record-date { font-size: 0.8rem; color: var(--muted-foreground); }

        .btn-small {
          margin-left: auto;
          color: var(--primary);
          font-weight: 600;
          font-size: 0.85rem;
        }

        .notif-item {
          display: flex;
          gap: 0.75rem;
          padding-bottom: 1rem;
          border-bottom: 1px solid #f1f5f9;
        }

        .notif-icon { color: var(--accent); flex-shrink: 0; }
        .notif-item p { font-size: 0.9rem; line-height: 1.4; color: var(--muted-foreground); }

        @media (max-width: 768px) {
          .card-inner { flex-direction: column; text-align: center; }
          .details-section { grid-template-columns: 1fr; }
          .primary-text { font-size: 1.4rem; }
        }
      `}</style>
    </DashboardLayout>
  );
};

export default PatientDashboard;
