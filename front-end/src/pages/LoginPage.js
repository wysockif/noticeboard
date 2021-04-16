import React, { Component } from 'react';
import InputWithValidation from '../components/InputWithValidation';
import { Container, Alert } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import ButtonWithSpinner from '../components/ButtonWithSpinner';


export class LoginPage extends Component {
    state = {
        username: '',
        password: '',
        alertMessage: undefined,
        ongoingApiCall: false
    }

    onClickLoginButton = () => {
        const body = {
            username: this.state.username,
            password: this.state.password
        }
        this.setState({ ongoingApiCall: true })
        this.props.actions.postLogin(body)
            .then(() => {
                this.setState({ ongoingApiCall: false })
            })
            .catch(error => {
                this.setState({ ongoingApiCall: false })
                if (error.response) {
                    this.setState({ alertMessage: error.response.data.message })
                }
            });
    }

    onChangeUsername = event => {
        this.setState({ username: event.target.value, alertMessage: '' });
    }

    onChangePassword = event => {
        this.setState({ password: event.target.value, alertMessage: '' });
    }

    render() {
        const disableSubmit = this.state.username && this.state.password ? false : true;

        return (
            <Container className="col-11 col-sm-10 col-md-7 col-lg-6">
                <h1 className="text-center my-4">Zaloguj się</h1>
                <InputWithValidation
                    label="Nazwa użytkownika:" placeholder="Nazwa użytkownika" icon="at"
                    value={this.state.username}
                    onChange={this.onChangeUsername}
                />

                <InputWithValidation
                    label="Hasło:" placeholder="Hasło" type="password" icon="key"
                    value={this.state.password}
                    onChange={this.onChangePassword}
                />

                {this.state.alertMessage && <Alert variant="danger" className="text-center">
                    {this.state.alertMessage}
                    <FontAwesomeIcon icon={["far", "frown-open"]} className="ms-1" />
                </Alert>}

                <div className="mb-3 text-center" >
                    <ButtonWithSpinner
                        onClick={this.onClickLoginButton}
                        disabled={disableSubmit || this.state.ongoingApiCall}
                        content="Zaloguj się"
                        ongoingApiCall={this.state.ongoingApiCall}
                    />
                </div>


            </Container>
        )
    }
}
LoginPage.defaultProps = {
    actions: {
        postLogin: () => new Promise((resolve, reject) => { })
    }
}
export default LoginPage;