import React, { Component } from 'react';
import pageNotFoundImage from '../assets/page-not-found.jpeg';
import { Image } from 'react-bootstrap';
import { Link } from 'react-router-dom';

class ErrorPage extends Component {
    render() {
        return (
            <div data-testid="errorpage" className="text-center my-5">
                <div>
                    <Image src={pageNotFoundImage} alt="Page not found" md={4} fluid />
                </div>
                <div>
                    <Link to="/"
                        className="btn btn-outline-light my-2 px-4"
                        style={{ backgroundColor: '#B84' }}
                        variant="outline-light">Powrót</Link>
                </div>
            </div >
        )
    };
};

export default ErrorPage;