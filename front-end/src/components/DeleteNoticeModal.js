import React from 'react';
import {Alert, Button, Modal} from "react-bootstrap";
import ButtonWithSpinner from "./ButtonWithSpinner";

const DeleteNoticeModal = ({show, ongoingApiCall, errorMessage, onClickDelete, onClickCancel}) => {
    return (
        <Modal show={show} onHide={onClickCancel} centered>
            <Modal.Header closeButton>
                <Modal.Title>Kasowanie ogłoszenia</Modal.Title>
            </Modal.Header>
            <Modal.Body>Czy na pewno chcesz skasować to ogłoszenie?</Modal.Body>
            {errorMessage && <Alert variant="danger" className="text-center">{errorMessage}</Alert>}
            <Modal.Footer>
                <Button variant="secondary" onClick={onClickCancel}>
                    Anuluj
                </Button>
                <ButtonWithSpinner onClick={onClickDelete} content="Skasuj" ongoingApiCall={ongoingApiCall}/>
            </Modal.Footer>
        </Modal>
    );
};

export default DeleteNoticeModal;
