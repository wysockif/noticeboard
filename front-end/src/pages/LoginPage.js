import React, { Component } from 'react';
import InputWithValidation from '../components/InputWithValidation';
import { Container, Button /*, Spinner*/ } from 'react-bootstrap';

export class LoginPage extends Component {


    render() {
        return (
            <Container className="col-11 col-sm-9 col-md-7 col-lg-5 col-xl-4">
                <h1 className="text-center my-4">Zaloguj się</h1>
                <InputWithValidation
                    label="Nazwa użytkownika:" placeholder="Nazwa użytkownika" icon="at"
                // value={this.state.username}
                // onChange={this.onChangeUsername}
                // hasError={this.state.errors.username !== undefined}
                // error={this.state.errors.username}
                />

                <InputWithValidation
                    label="Hasło:" placeholder="Hasło" type="password" icon="key"
                // value={this.state.password}
                // onChange={this.onChangePassword}
                // hasError={this.state.errors.password !== undefined}
                // error={this.state.errors.password}
                />

                <div className="mb-3 text-center" >
                    <Button style={{ backgroundColor: '#B84' }} variant="outline-light" 
                        // onClick={this.onClickRegister}
                        // disabled={this.state.pendingApiCall || !this.state.isPasswordRepeatCorrect}
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

export default LoginPage;