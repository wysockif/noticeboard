import React, {Component} from 'react';
import * as apiCalls from '../api/apiCalls';

class UserProfilePage extends Component {
    state = {
        user: undefined
    }

    componentDidMount() {
        const username = this.props.match.params.username;
        if (!username) {
            return;
        }
        apiCalls.getUser(username)
            .then(response => {
               this.setState({user: response.data});
            });
    }

    render() {
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