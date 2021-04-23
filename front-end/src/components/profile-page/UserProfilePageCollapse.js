import React, {Component} from 'react';
import {Collapse, Container} from "react-bootstrap";
import InputWithValidation from "../InputWithValidation";
import ButtonWithSpinner from "../ButtonWithSpinner";

class UserProfilePageCollapse extends Component {
    state = {
        firstName: '',
        lastName: ''
    }

    componentDidMount() {
        if (this.props.user) {
            this.setState({firstName: this.props.user.firstName, lastName: this.props.user.lastName})
        }
    }

    onChangeFirstName = event => {
        this.setState({firstName: event.target.value});
    }

    onChangeLastName = event => {
        this.setState({lastName: event.target.value});
    }

    render() {
        return (
            <div>
                <Collapse in={this.props.open}>
                    <div id="example-collapse-text" className="mt-3">
                        <Container className="col-12 col-sm-11 col-md-10 col-lg-9 col-xl-8">

                            <InputWithValidation
                                label="Imię:" placeholder="Imię" icon="user" width="120px"
                                value={this.state.firstName}
                                onChange={this.onChangeFirstName}
                            />
                            <InputWithValidation
                                label="Nazwisko:" placeholder="Nazwisko" icon="user" width="120px"
                                value={this.state.lastName}
                                onChange={this.onChangeLastName}
                            />
                            <ButtonWithSpinner
                                content="Zatwierdź zmiany"
                            />
                        </Container>
                    </div>
                </Collapse>
            </div>
        );
    }
}

export default UserProfilePageCollapse;