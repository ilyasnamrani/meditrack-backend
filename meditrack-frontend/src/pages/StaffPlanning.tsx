import { useState, useEffect } from 'react';
import DashboardLayout from '../layouts/DashboardLayout';
import { Calendar, Plus, X, Loader2, Clock, User, CheckCircle, AlertCircle } from 'lucide-react';
import { getToken } from '../services/keycloak';
import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8888', // Gateway
});

const StaffPlanning = () => {
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [status, setStatus] = useState<'idle' | 'loading' | 'success' | 'error'>('idle');
    const [message, setMessage] = useState('');
    const [appointments, setAppointments] = useState<any[]>([]);
    const [loading, setLoading] = useState(true);

    const [formData, setFormData] = useState({
        patientId: '',
        date: '',
        time: '',
        type: 'CONSULTATION', // enum: CONSULTATION, OPERATION, EXAMEN
        status: 'SCHEDULED'
    });

    useEffect(() => {
        fetchAppointments();
    }, []);

    const fetchAppointments = async () => {
        try {
            const response = await api.get('/api/planning', {
                headers: {
                    'Authorization': `Bearer ${getToken()}`
                }
            });
            setAppointments(response.data);
        } catch (error) {
            console.error("Error fetching planning:", error);
        } finally {
            setLoading(false);
        }
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setStatus('loading');

        // Combine date and time
        const dateTime = `${formData.date}T${formData.time}:00`;

        try {
            await api.post('/api/planning', {
                patientId: Number(formData.patientId),
                startTime: dateTime,
                type: formData.type,
                status: formData.status
            }, {
                headers: {
                    'Authorization': `Bearer ${getToken()}`
                }
            });

            setStatus('success');
            setMessage('Rendez-vous créé avec succès !');
            fetchAppointments(); // Refresh list
            setTimeout(() => {
                setIsModalOpen(false);
                setStatus('idle');
                setFormData({ patientId: '', date: '', time: '', type: 'CONSULTATION', status: 'SCHEDULED' });
            }, 2000);
        } catch (err: any) {
            setStatus('error');
            setMessage(err.response?.data?.message || "Erreur lors de la création.");
        }
    };

    return (
        <DashboardLayout>
            <div className="page-container">
                <header className="page-header">
                    <div>
                        <h1>Planning & Rendez-vous</h1>
                        <p>Gérez les emplois du temps et les interventions.</p>
                    </div>
                    <button className="btn-primary" onClick={() => setIsModalOpen(true)}>
                        <Plus size={20} /> Nouveau Rendez-vous
                    </button>
                </header>

                {loading ? (
                    <div className="loading-state"><Loader2 className="spin" size={40} /></div>
                ) : (
                    <div className="appointments-grid">
                        {appointments.length === 0 ? (
                            <div className="empty-state">
                                <Calendar size={48} />
                                <p>Aucun rendez-vous prévu.</p>
                            </div>
                        ) : (
                            appointments.map((apt: any) => (
                                <div key={apt.id} className="appointment-card">
                                    <div className="apt-header">
                                        <span className={`apt-type type-${apt.type.toLowerCase()}`}>{apt.type}</span>
                                        <span className={`apt-status status-${apt.status.toLowerCase()}`}>{apt.status}</span>
                                    </div>
                                    <div className="apt-body">
                                        <div className="apt-row">
                                            <User size={16} />
                                            <span>Patient ID: {apt.patientId}</span>
                                        </div>
                                        <div className="apt-row">
                                            <Clock size={16} />
                                            <span>{new Date(apt.startTime).toLocaleString()}</span>
                                        </div>
                                    </div>
                                </div>
                            ))
                        )}
                    </div>
                )}

                {isModalOpen && (
                    <div className="modal-overlay">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h2>Planifier un Rendez-vous</h2>
                                <button onClick={() => setIsModalOpen(false)} className="close-btn"><X size={24} /></button>
                            </div>

                            {status === 'success' ? (
                                <div className="success-message">
                                    <CheckCircle size={48} />
                                    <p>{message}</p>
                                </div>
                            ) : (
                                <form onSubmit={handleSubmit} className="modal-form">
                                    <div className="form-group">
                                        <label>ID Patient</label>
                                        <input type="number" required value={formData.patientId} onChange={(e) => setFormData({ ...formData, patientId: e.target.value })} placeholder="Ex: 123" />
                                    </div>

                                    <div className="form-row">
                                        <div className="form-group">
                                            <label>Date</label>
                                            <input type="date" required value={formData.date} onChange={(e) => setFormData({ ...formData, date: e.target.value })} />
                                        </div>
                                        <div className="form-group">
                                            <label>Heure</label>
                                            <input type="time" required value={formData.time} onChange={(e) => setFormData({ ...formData, time: e.target.value })} />
                                        </div>
                                    </div>

                                    <div className="form-group">
                                        <label>Type</label>
                                        <select value={formData.type} onChange={(e) => setFormData({ ...formData, type: e.target.value })}>
                                            <option value="CONSULTATION">Consultation</option>
                                            <option value="OPERATION">Opération</option>
                                            <option value="EXAMEN">Examen</option>
                                        </select>
                                    </div>

                                    {status === 'error' && <div className="error-message"><AlertCircle size={16} /> {message}</div>}

                                    <button type="submit" className="submit-btn" disabled={status === 'loading'}>
                                        {status === 'loading' ? <Loader2 className="spin" size={20} /> : 'Confirmer le Rendez-vous'}
                                    </button>
                                </form>
                            )}
                        </div>
                    </div>
                )}
            </div>

            <style>{`
        .page-container { padding: 1rem; }
        .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem; }
        .page-header h1 { font-size: 1.8rem; font-weight: 700; color: var(--foreground); }
        .page-header p { color: var(--muted-foreground); }
        
        .btn-primary { background: var(--primary); color: white; padding: 0.75rem 1.5rem; border-radius: 8px; font-weight: 600; display: flex; align-items: center; gap: 0.5rem; transition: all 0.2s; }
        .btn-primary:hover { opacity: 0.9; transform: translateY(-1px); }

        .loading-state, .empty-state { padding: 4rem; text-align: center; color: var(--muted-foreground); display: flex; flex-direction: column; align-items: center; gap: 1rem; }
        .spin { animation: spin 1s linear infinite; }
        
        .appointments-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 1.5rem; }
        .appointment-card { background: white; border: 1px solid var(--border); border-radius: 12px; padding: 1.5rem; box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
        
        .apt-header { display: flex; justify-content: space-between; margin-bottom: 1rem; }
        .apt-type, .apt-status { font-size: 0.75rem; font-weight: 700; padding: 0.25rem 0.75rem; border-radius: 100px; text-transform: uppercase; }
        
        .type-consultation { background: #e0f2fe; color: #0369a1; }
        .type-operation { background: #fee2e2; color: #b91c1c; }
        .type-examen { background: #f3e8ff; color: #7e22ce; }
        
        .status-scheduled { background: #fef9c3; color: #854d0e; }
        .status-completed { background: #dcfce7; color: #166534; }
        .status-cancelled { background: #f3f4f6; color: #374151; }

        .apt-body { display: flex; flex-direction: column; gap: 0.5rem; }
        .apt-row { display: flex; align-items: center; gap: 0.5rem; font-size: 0.9rem; color: var(--foreground); }
        .apt-row svg { color: var(--muted-foreground); }

        /* Modal */
        .modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.5); backdrop-filter: blur(4px); display: flex; justify-content: center; align-items: center; z-index: 50; }
        .modal-content { background: white; width: 90%; max-width: 500px; padding: 2rem; border-radius: 16px; box-shadow: 0 20px 25px -5px rgba(0,0,0,0.1); }
        .modal-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; }
        .modal-header h2 { font-size: 1.25rem; font-weight: 700; }
        .close-btn { color: var(--muted-foreground); cursor: pointer; }
        
        .modal-form { display: flex; flex-direction: column; gap: 1rem; }
        .form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; }
        .form-group { display: flex; flex-direction: column; gap: 0.5rem; }
        .form-group label { font-size: 0.875rem; font-weight: 500; }
        .form-group input, .form-group select { padding: 0.75rem; border: 1px solid var(--border); border-radius: 8px; background: #f8fafc; }
        
        .submit-btn { background: var(--primary); color: white; padding: 0.75rem; border-radius: 8px; font-weight: 700; margin-top: 1rem; }
        .submit-btn:disabled { opacity: 0.7; cursor: not-allowed; }
        
        .success-message { text-align: center; color: var(--success); padding: 2rem; display: flex; flex-direction: column; align-items: center; gap: 1rem; }
        .error-message { background: #fee2e2; color: #b91c1c; padding: 0.75rem; border-radius: 8px; display: flex; align-items: center; gap: 0.5rem; font-size: 0.875rem; }

        @keyframes spin { 100% { transform: rotate(360deg); } }
       `}</style>
        </DashboardLayout>
    );
};

export default StaffPlanning;
