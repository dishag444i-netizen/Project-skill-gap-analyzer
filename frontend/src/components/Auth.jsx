
import React, { useState } from 'react';
import { apiRequest } from '../api';
import './Auth.css';

function Auth({ onLogin }) {
    const [isRegister, setIsRegister] = useState(false);
    const [isForgotPassword, setIsForgotPassword] = useState(false);

    const [fullName, setFullName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');

    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');
        setLoading(true);

        try {
            if (isForgotPassword) {
                const response = await apiRequest('/api/users/forgot-password', {
                    method: 'POST',
                    body: JSON.stringify({ email })
                });

                setSuccess(response.message);
                return;
            }

            if (isRegister) {
                await apiRequest('/api/users/register', {
                    method: 'POST',
                    body: JSON.stringify({
                        fullName,
                        email,
                        password
                    })
                });
            }

            const response = await apiRequest('/api/users/login', {
                method: 'POST',
                body: JSON.stringify({
                    email,
                    password
                })
            });

            localStorage.setItem('token', response.token);
            localStorage.setItem('user', JSON.stringify(response.user));

            onLogin();

        } catch (err) {
            setError(err.message || 'Something went wrong. Please try again.');
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

                <h2>
                    {isForgotPassword
                        ? 'Forgot Password?'
                        : isRegister
                            ? 'Create your account'
                            : 'Welcome back!'}
                </h2>

                <p className="auth-subtitle">
                    {isForgotPassword
                        ? 'Enter your registered email to receive a password reset link.'
                        : isRegister
                            ? 'Start building your career with SkillPath.'
                            : 'Login to continue your learning journey.'}
                </p>

                <form onSubmit={handleSubmit}>

                    {isRegister && !isForgotPassword && (
                        <div className="auth-field">
                            <label>Full Name</label>
                            <input
                                type="text"
                                placeholder="Enter your full name"
                                value={fullName}
                                onChange={(e) => setFullName(e.target.value)}
                                required
                            />
                        </div>
                    )}

                    <div className="auth-field">
                        <label>Email Address</label>
                        <input
                            type="email"
                            placeholder="Enter your email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                        />
                    </div>

                    {!isForgotPassword && (
                        <div className="auth-field">
                            <label>Password</label>
                            <input
                                type="password"
                                placeholder="Enter your password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                minLength={6}
                                required
                            />
                        </div>
                    )}

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
                        {loading
                            ? 'Please wait...'
                            : isForgotPassword
                                ? 'Send Reset Link'
                                : isRegister
                                    ? 'Create Account'
                                    : 'Login'}
                    </button>

                </form>

                {!isRegister && !isForgotPassword && (
                    <p className="auth-switch">
                        <button
                            type="button"
                            onClick={() => {
                                setIsForgotPassword(true);
                                setError('');
                                setSuccess('');
                            }}
                        >
                            Forgot Password?
                        </button>
                    </p>
                )}

                <p className="auth-switch">
                    {isForgotPassword
                        ? 'Remember your password?'
                        : isRegister
                            ? 'Already have an account?'
                            : "Don't have an account?"}

                    <button
                        type="button"
                        onClick={() => {
                            setIsForgotPassword(false);
                            setIsRegister(
                                isForgotPassword ? false : !isRegister
                            );
                            setError('');
                            setSuccess('');
                        }}
                    >
                        {isForgotPassword
                            ? 'Login'
                            : isRegister
                                ? 'Login'
                                : 'Register'}
                    </button>
                </p>

            </div>
        </div>
    );
}

export default Auth;
