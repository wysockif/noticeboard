import React, {Component} from 'react';
import * as apiCalls from '../api/apiCalls';
import ErrorAlert from "../components/ErrorAlert";
import userNotFoundImage from '../assets/user-not-found.jpeg';


class UserProfilePage extends Component {
    state = {
        errorMessage: false,
        user: undefined
    }

    componentDidMount() {
        this.loadUserToState();
    }

    componentDidUpdate(prevProps, prevState, snapshot) {
        if (this.props.match.params.username !== prevProps.match.params.username) {
            this.loadUserToState();
        }
    }

    loadUserToState() {
        const username = this.props.match.params.username;
        if (!username) {
            return;
        }
        this.setState({errorMessage: false});
        apiCalls.getUser(username)
            .then(response => {
                this.setState({user: response.data});
            })
            .catch(error => {
                this.setState({errorMessage: true})
            });
    }

    userNotFoundAlert() {
        return (
            <ErrorAlert image={userNotFoundImage}/>
        );
    }

    render() {
        if (this.state.errorMessage) {
            return this.userNotFoundAlert();
        }
        return (
            <div data-testid="userprofilepage" className="text-center">
                {this.state.user && <div>{this.state.user.firstName} {this.state.user.lastName}</div>}
            </div>
        );
    }
}

UserProfilePage.defaultProps = {
    match: {
        params: {}
    }
};

export default UserProfilePage;