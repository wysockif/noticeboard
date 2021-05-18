import React from 'react';
import {Image} from "react-bootstrap";
import {Link} from "react-router-dom";

const ErrorAlert = props => {
    return (
        <div className="text-center my-5" data-testid="error-alert">
            <div>
                <Image src={props.image} alt="Page not found" md={4} fluid/>
            </div>
            <div>
                <Link to="/"
                      className="btn btn-outline-light my-2 px-4"
                      style={{backgroundColor: '#b78e56'}}
                      variant="outline-light">Powrót</Link>
            </div>
        </div>
    );
}

export default ErrorAlert;