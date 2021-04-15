import React from 'react';
import { Button, Spinner } from 'react-bootstrap';

const ButtonWithSpinner = props => {
    return (
        <Button style={{ backgroundColor: '#B84' }} variant="outline-light" onClick={props.onClick}
            disabled={props.disabled}
        >
            {props.content}
            {props.ongoingApiCall && <Spinner animation="border" size="sm" role="status" className="ms-1">
                <span className="sr-only">Loading...</span>
            </Spinner>}
        </Button>

    );
};

export default ButtonWithSpinner;