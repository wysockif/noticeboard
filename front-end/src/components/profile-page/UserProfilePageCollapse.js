import React, {Component} from 'react';
import {Collapse, Container} from 'react-bootstrap';
import InputWithValidation from '../InputWithValidation';
import ButtonWithSpinner from '../ButtonWithSpinner';


class UserProfilePageCollapse extends Component {
    state = {
        id: undefined,
        firstName: '',
        lastName: ''
    }

    componentDidMount() {
        if (this.props.user) {
            this.setState({
                id: this.props.user.id,
                firstName: this.props.user.firstName,
                lastName: this.props.user.lastName,
            })
        }
    }

    onChangeFirstName = event => {
        delete this.props.errors.firstName;
        this.setState({firstName: event.target.value});
    }

    onChangeLastName = event => {
        delete this.props.errors.lastName;
        this.setState({lastName: event.target.value});
    }

    onExitedCollapse = () => {
        if (this.props.user) {
            this.setState(
                {
                    firstName: this.props.user.firstName,
                    lastName: this.props.user.lastName
                });
        }
    }
    onClickSubmitButton = () => {
        const body = {
            firstName: this.state.firstName,
            lastName: this.state.lastName
        }
        this.props.onClickUpdateUser(this.state.id, body);
    }


    render() {
        return (
            <div>
                <Collapse in={this.props.open} onExited={this.onExitedCollapse}>
                    <div id="example-collapse-text" className="mt-3">
                        <Container className="col-12 col-sm-11 col-md-10 col-lg-9 col-xl-8">
                            <div className="mb-2">Dane osobowe</div>
                            <InputWithValidation
                                label="Imię" placeholder="Imię" width="115px"
                                value={this.state.firstName}
                                onChange={this.onChangeFirstName}
                                hasError={this.props.errors.firstName !== undefined}
                                error={this.props.errors.firstName}
                            />
                            <InputWithValidation
                                label="Nazwisko" placeholder="Nazwisko" width="115px"
                                value={this.state.lastName}
                                onChange={this.onChangeLastName}
                                hasError={this.props.errors.lastName !== undefined}
                                error={this.props.errors.lastName}
                            />

                            <div className="mb-2">Zdjęcie profilowe</div>
                            <div className="input-group mb-3">
                                <input type="file" className="form-control" id="inputGroupFile01"/>
                            </div>

                            <ButtonWithSpinner
                                content="Zatwierdź zmiany"
                                onClick={this.onClickSubmitButton}
                                disabled={
                                    this.props.ongoingApiCall ||
                                    this.state.lastName === '' ||
                                    this.state.firstName === ''
                                }
                                ongoingApiCall={this.props.ongoingApiCall}
                            />
                        </Container>
                    </div>
                </Collapse>
            </div>
        );
    }
}
UserProfilePageCollapse.defaultProps = {
    errors: []
}


export default UserProfilePageCollapse;