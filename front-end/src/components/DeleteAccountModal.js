import React, {Component} from 'react';
import {Button, Modal} from "react-bootstrap";
import InputWithValidation from "./InputWithValidation";
import ButtonWithSpinner from "./ButtonWithSpinner";
import * as apiCalls from "../api/apiCalls";
import {withRouter} from "react-router-dom";
import {connect} from "react-redux";


class DeleteAccountModal extends Component {

    state = {
        ongoingApiCall: false,
        disabledButton: true,
        password: '',
        errorMessage: undefined
    }


    onCancel = () => {
        this.setState({
            ongoingApiCall: false,
            errorMessage: undefined,
            disabledButton: true,
            password: ''
        });
        this.props.onClickCancel();
    }

    onClickDelete = event => {
        this.setState({ongoingApiCall: true, errorMessage: undefined})
        const password = this.state.password;
        apiCalls.deleteUserAccount(this.props.id, {data: {password}})
            .then(() => {
                this.setState({ongoingApiCall: false});
                const logoutAction = {
                    type: 'LOGOUT_SUCCESS'
                }
                this.props.dispatch(logoutAction);
                this.props.history.replace('/');
            })
            .catch(apiError => {
                console.log(apiError)
                this.setState({ongoingApiCall: false});
                if (apiError.response && apiError.response.data && apiError.response.data.validationErrors && apiError.response.data.validationErrors.password) {
                    this.setState({errorMessage: apiError.response.data.validationErrors.password});
                } else if (apiError.response && apiError.response.data && apiError.response.data.message) {
                    this.setState({errorMessage: apiError.response.data.message});
                }
            })
    }

    onChangePassword = event => {
        if (this.state.password !== event.target.value.trim()) {
            this.setState({password: event.target.value});
            if (this.state.password.length > 6) {
                this.setState({disabledButton: false})
            } else {
                this.setState({disabledButton: true})
            }
        }
    };

    render() {

        return (
            <Modal show={this.props.isDisplayed} onHide={this.onCancel} centered className="open-model">
                <Modal.Header closeButton>
                    <Modal.Title>Kasowanie konta</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <div>Czy na pewno chcesz nieodwracalnie skasować konto?</div>
                    <small className="text-muted">Wszystkie Twoje ogłoszenia również zostaną usunięte.</small>
                </Modal.Body>
                <div className="col-sm-10 mx-auto">
                    <InputWithValidation
                        label="Wprowadź hasło:" placeholder="Hasło" type="password" icon="key"
                        value={this.state.password}
                        onChange={this.onChangePassword}
                        hasError={this.state.errorMessage !== undefined}
                        error={this.state.errorMessage}
                    />
                </div>
                <Modal.Footer>
                    <Button variant="secondary" onClick={this.onCancel}>
                        Anuluj
                    </Button>
                    <ButtonWithSpinner disabled={this.state.disabledButton} onClick={this.onClickDelete}
                                       content="Skasuj konto"
                                       ongoingApiCall={this.state.ongoingApiCall}/>
                </Modal.Footer>
            </Modal>
        )
    }

}

export default withRouter(connect()(DeleteAccountModal));
