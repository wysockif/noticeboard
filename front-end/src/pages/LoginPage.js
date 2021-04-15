import React, { Component } from 'react';
import InputWithValidation from '../components/InputWithValidation';
import { Container, Button, Alert /*, Spinner*/ } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';


export class LoginPage extends Component {
    state = {
        username: '',
        password: '',
        alertMessage: undefined
    }

    onClickLoginButton = () => {
        const body = {
            username: this.state.username,
            password: this.state.password
        }
        this.props.actions.postLogin(body)
            .catch(error => {
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
            <Container className="col-11 col-sm-9 col-md-7 col-lg-5 col-xl-4">
                <h1 className="text-center my-4">Zaloguj się</h1>
                <InputWithValidation
                    label="Nazwa użytkownika:" placeholder="Nazwa użytkownika" icon="at"
                    value={this.state.username}
                    onChange={this.onChangeUsername}
                // hasError={this.state.errors.username !== undefined}
                // error={this.state.errors.username}
                />

                <InputWithValidation
                    label="Hasło:" placeholder="Hasło" type="password" icon="key"
                    value={this.state.password}
                    onChange={this.onChangePassword}
                // hasError={this.state.errors.password !== undefined}
                // error={this.state.errors.password}
                />

                {this.state.alertMessage && <Alert variant="danger" className="text-center">
                    {this.state.alertMessage}
                    <FontAwesomeIcon icon={["far", "frown-open"]} className="ms-1" />
                </Alert>}

                <div className="mb-3 text-center" >
                    <Button style={{ backgroundColor: '#B84' }} variant="outline-light"
                        onClick={this.onClickLoginButton}
                        disabled={disableSubmit}
                    >
                        Zaloguj się
                        {/* {this.state.pendingApiCall && <Spinner animation="border" size="sm" role="status" className="ms-1">
                            <span className="sr-only">Loading...</span>
                        </Spinner>} */}
                    </Button>
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