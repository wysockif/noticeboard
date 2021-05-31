import React, {Component} from 'react';
import {Button, Modal} from "react-bootstrap";
import InputWithValidation from "./InputWithValidation";
import ButtonWithSpinner from "./ButtonWithSpinner";
import * as apiCalls from "../api/apiCalls";
import {withRouter} from "react-router-dom";
import {connect} from "react-redux";

class ChangePasswordModal extends Component {

    state = {
        ongoingApiCall: false,
        disabledButton: true,
        oldPassword: '',
        newPassword: '',
        newPasswordRepeat: '',
        oldPasswordErrorMessage: undefined,
        newPasswordErrorMessage: undefined,
        isPasswordRepeatCorrect: true,
        successInfo: undefined
    }


    onCancel = () => {
        this.setState({
            ongoingApiCall: false,
            oldPasswordErrorMessage: undefined,
            newPasswordErrorMessage: undefined,
            disabledButton: true,
            oldPassword: '',
            newPassword: '',
            newPasswordRepeat: ''
        });
        this.props.onClickCancel();
    }

    onClickChangePassword = () => {
        this.setState({
            ongoingApiCall: true,
            oldPasswordErrorMessage: undefined,
            newPasswordErrorMessage: undefined
        });
        const body = {
            oldPassword: this.state.oldPassword,
            newPassword: this.state.newPassword
        }
        apiCalls.changePassword(this.props.id, body)
            .then(() => {
                this.setState({ongoingApiCall: false, successInfo: true});
                const changePasswordAction = {
                    type: 'CHANGE_PASSWORD_SUCCESS',
                    payload: {
                        password: body.newPassword
                    }
                }
                setTimeout(() => {
                    this.props.dispatch(changePasswordAction);
                    this.props.history.replace(`/user/${this.props.username}`);
                }, 1000);

            })
            .catch(apiError => {
                this.setState({ongoingApiCall: false})
                if (apiError.response.status === 401) {
                    this.setState({oldPasswordErrorMessage: apiError.response.data.message});
                } else if (apiError.response.status === 400) {
                    if (apiError.response.data && apiError.response.data.validationErrors && apiError.response.data.validationErrors.newPassword) {
                        this.setState({newPasswordErrorMessage: apiError.response.data.validationErrors.newPassword});
                    } else if (apiError.response.data && apiError.response.data.validationErrors && apiError.response.data.validationErrors.oldPassword) {
                        this.setState({oldPasswordErrorMessage: apiError.response.data.validationErrors.oldPassword});
                    } else {
                        this.setState({newPasswordErrorMessage: apiError.response.data.message});
                    }
                }
            })
    }

    checkIfPasswordsAreLongEnough = () => {
        if (this.state.newPassword.length > 6 && this.state.newPasswordRepeat.length > 6
            && this.state.oldPassword.length > 6 && this.state.isPasswordRepeatCorrect) {
            this.setState({disabledButton: false})
        } else {
            this.setState({disabledButton: true})
        }
    }

    onChangeOldPassword = event => {
        if (this.state.oldPassword !== event.target.value.trim()) {
            this.setState({oldPassword: event.target.value.trim(), oldPasswordErrorMessage: undefined}, () => {
                this.checkIfPasswordsAreLongEnough();
            });
        }
    }

    onChangeNewPassword = event => {
        if (this.state.newPassword !== event.target.value.trim()) {
            const isPasswordRepeatCorrect = event.target.value.trim() === this.state.newPasswordRepeat;
            this.setState({
                newPassword: event.target.value.trim(),
                isPasswordRepeatCorrect,
                newPasswordErrorMessage: undefined
            }, () => {
                this.checkIfPasswordsAreLongEnough();
            });
        }
    }

    onChangeNewPasswordRepeat = event => {
        if (this.state.newPasswordRepeat !== event.target.value.trim()) {
            const isPasswordRepeatCorrect = this.state.newPassword === event.target.value.trim();
            this.setState({
                newPasswordRepeat: event.target.value.trim(),
                isPasswordRepeatCorrect
            }, () => {
                this.checkIfPasswordsAreLongEnough();
            });
        }
    }

    render() {
        return (
            <Modal show={this.props.isDisplayed} onHide={this.onCancel} centered className="open-model">
                <Modal.Header closeButton>
                    <Modal.Title>Zmiana hasła</Modal.Title>
                </Modal.Header>
                {this.state.successInfo &&
                <div className="text-center text-success my-3">Hasło zostało zmienione pomyślnie</div>}
                {!this.state.successInfo && <div className="col-sm-10 mx-auto mt-3">
                    <InputWithValidation
                        label="Bieżące hasło:" placeholder="Bieżące hasło" type="password" icon="key"
                        value={this.state.oldPassword}
                        onChange={this.onChangeOldPassword}
                        hasError={this.state.oldPasswordErrorMessage !== undefined}
                        error={this.state.oldPasswordErrorMessage}
                    />
                    <InputWithValidation
                        label="Nowe hasło:" placeholder="Nowe hasło" type="password" icon="key"
                        value={this.state.newPassword}
                        onChange={this.onChangeNewPassword}
                        hasError={this.state.newPasswordErrorMessage !== undefined}
                        error={this.state.newPasswordErrorMessage}
                    />
                    <InputWithValidation
                        label="Powtórz nowe hasło:" placeholder="Powtórz nowe hasło" type="password" icon="key"
                        value={this.state.newPasswordRepeat}
                        onChange={this.onChangeNewPasswordRepeat}
                        hasError={!this.state.isPasswordRepeatCorrect && this.state.newPasswordRepeat !== ''}
                        error={'Hasła nie są identyczne'}
                    />
                </div>}
                {!this.state.successInfo && <Modal.Footer as="div">
                    <Button variant="secondary" onClick={this.onCancel}>
                        Anuluj
                    </Button>
                    <ButtonWithSpinner disabled={this.state.disabledButton} onClick={this.onClickChangePassword}
                                       content="Zmień hasło"
                                       ongoingApiCall={this.state.ongoingApiCall}/>
                </Modal.Footer>}
            </Modal>
        )
    }

}

export default withRouter(connect()(ChangePasswordModal));
