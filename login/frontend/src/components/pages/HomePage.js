import React, { useEffect, useState } from 'react';
import { Link, withRouter } from 'react-router-dom';

function HomePage(props) {
    const [data, setData] = useState([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {

        const token = localStorage.getItem('token');

        if (token) {
            const fetchData = async () => {
                try {
                    setLoading(true);
                    const token = localStorage.getItem('token');
                    const response = await fetch('http://localhost:8000/listuser', {
                        headers: {
                            'Authorization': `Bearer ${token}`
                        }
                    });
                    const data = await response.json();
                    setData(data);
                } catch (error) {
                    console.error(error);
                } finally {
                    setLoading(false);
                }
            };
            fetchData();
        } else {
            setLoading(false);
            props.history.push("/login");
        }

    }, [props.history]);


    const handleLogout = () => {
        localStorage.removeItem('token');
    };

    return (
        <div className="text-center">
            <h1 className="main-title home-page-title">welcome to our app</h1>
            <Link to="/">
                <button onClick={handleLogout} className="primary-button">
                    Log out
                </button>
            </Link>
            <div>
                {loading ? (
                    <p>Loading...</p>
                ) : (
                    <ul>
                        {data.map((item) => (
                            <li key={item.username}>
                                <p>Username: {item.username}</p>
                                <p>Email: {item.email}</p>
                                <p>Is Staff: {item.is_staff ? 'Yes' : 'No'}</p>
                            </li>
                        ))}
                    </ul>
                )}
            </div>
        </div>
    );
}

export default withRouter(HomePage)