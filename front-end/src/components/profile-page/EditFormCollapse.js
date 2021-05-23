import React, {Component} from 'react';
import {Button, Collapse, Container} from 'react-bootstrap';
import InputWithValidation from '../InputWithValidation';
import ButtonWithSpinner from '../ButtonWithSpinner';
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import DeleteAccountModal from "../DeleteAccountModal";


class EditFormCollapse extends Component {
    state = {
        id: undefined,
        firstName: '',
        lastName: '',
        selectedFile: '',
        deleteAccountModal: false
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
        this.setState({selectedFile: ''});
    }

    onClickSubmitButton = () => {
        const body = {
            firstName: this.state.firstName,
            lastName: this.state.lastName
        }
        this.props.onClickUpdateUser(this.state.id, body);
    }

    onSelectProfileImage = event => {
        this.props.onImageSelect(event);
        this.setState({selectedFile: event.target.value})
    }

    onClickCancelDeleteAccount = () => {
        this.setState({deleteAccountModal: false})
    }

    onClickDeleteAccountButton = () => {
        this.setState({deleteAccountModal: true})
    }

    render() {
        let disabled = this.props.ongoingApiCall ||
            this.state.lastName === '' ||
            this.state.firstName === '' ||
            this.props.errors.hasOwnProperty('firstName') ||
            this.props.errors.hasOwnProperty('lastName') ||
            this.props.errors.hasOwnProperty('profileImage');

        return (
            <div>
                <DeleteAccountModal isDisplayed={this.state.deleteAccountModal}
                                    onClickCancel={this.onClickCancelDeleteAccount}
                                    id={this.state.id}
                                    onDeleteSuccess={this.onDeleteAccountSuccess}
                />
                <Collapse in={this.props.open} onExited={this.onExitedCollapse}>
                    <div id="example-collapse-text" className="mt-3">

                        <Container className="col-12 col-sm-11 col-md-10 col-lg-9 col-xl-8">
                            <div className="mb-2">
                                <FontAwesomeIcon icon="user" className="me-1"/>
                                Dane osobowe
                            </div>
                            <InputWithValidation
                                label="Imię:" placeholder="Imię" width="115px"
                                value={this.state.firstName}
                                onChange={this.onChangeFirstName}
                                hasError={this.props.errors.firstName !== undefined}
                                error={this.props.errors.firstName}
                            />
                            <InputWithValidation
                                label="Nazwisko:" placeholder="Nazwisko" width="115px"
                                value={this.state.lastName}
                                onChange={this.onChangeLastName}
                                hasError={this.props.errors.lastName !== undefined}
                                error={this.props.errors.lastName}
                            />

                            <div className="mb-2">
                                <FontAwesomeIcon icon="user-circle" className="me-1"/>
                                Zdjęcie profilowe
                            </div>
                            <div className="custom-file mb-3 mx-auto">
                                <div className="custom-file mb-3 mx-auto mt-2">
                                    <input className="form-control" type="file" id="formFile"
                                           onChange={this.onSelectProfileImage}/>
                                    {this.props.errors.profileImage !== undefined && <div className="text-center">
                                        <small className="text-danger">
                                            {this.props.errors.profileImage}
                                        </small>
                                    </div>}
                                </div>

                            </div>
                            <div>
                                <ButtonWithSpinner
                                    content="Zatwierdź zmiany"
                                    onClick={this.onClickSubmitButton}
                                    disabled={disabled}
                                    ongoingApiCall={this.props.ongoingApiCall}
                                />
                            </div>

                        </Container>
                        <div>
                            <hr style={{maxWidth: "280px", marginBottom: "1px"}} className="mx-auto"/>
                            <Button className="settings-btn mx-1 px-2" variant="outline-light" size="sm">
                                Zmień hasło
                            </Button>
                            <Button className="settings-btn mx-1 px-2" variant="outline-light" size="sm"
                                    onClick={this.onClickDeleteAccountButton}>
                                Skasuj konto
                            </Button>
                        </div>
                    </div>
                </Collapse>
            </div>
        );
    }
}

EditFormCollapse.defaultProps = {
    errors: []
}


export default EditFormCollapse;