
import React, { useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { apiRequest } from '../api';
import './Auth.css';

function ResetPassword() {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();

    const token = searchParams.get('token');

    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');

        if (!token) {
            setError('Invalid or missing password reset link.');
            return;
        }

        if (password.length < 6) {
            setError('Password must contain at least 6 characters.');
            return;
        }

        if (password !== confirmPassword) {
            setError('Passwords do not match.');
            return;
        }

        setLoading(true);

        try {
            const response = await apiRequest(
                `/api/users/reset-password?token=${encodeURIComponent(token)}&newPassword=${encodeURIComponent(password)}`,
                { method: 'POST' }
            );

            setSuccess(response.message || 'Password reset successful!');
            setTimeout(() => navigate('/'), 2000);

        } catch (err) {
            setError(err.message || 'Unable to reset password. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="auth-page">
            <div className="auth-card">

                <div className="auth-brand">
                    <div className="auth-logo">S</div>
                    <h1>SkillPath</h1>
                </div>

                <h2>Reset Your Password</h2>

                <p className="auth-subtitle">
                    Create a new password for your account.
                </p>

                <form onSubmit={handleSubmit}>

                    <div className="auth-field">
                        <label>New Password</label>
                        <input
                            type="password"
                            placeholder="Enter new password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            minLength={6}
                            required
                        />
                    </div>

                    <div className="auth-field">
                        <label>Confirm Password</label>
                        <input
                            type="password"
                            placeholder="Confirm new password"
                            value={confirmPassword}
                            onChange={(e) => setConfirmPassword(e.target.value)}
                            minLength={6}
                            required
                        />
                    </div>

                    {error && (
                        <p className="auth-error">{error}</p>
                    )}

                    {success && (
                        <p className="auth-success">{success}</p>
                    )}

                    <button
                        type="submit"
                        className="auth-submit"
                        disabled={loading}
                    >
                        {loading ? 'Please wait...' : 'Reset Password'}
                    </button>

                </form>

            </div>
        </div>
    );
}

export default ResetPassword;
