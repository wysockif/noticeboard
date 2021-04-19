import React, { Component } from 'react';

class UserProfilePage extends Component {
    render() {
        return (
            <div data-testid="userprofilepage" className="text-center">
                Ogłoszenia dodane przez zalogowanego użytkownika z możliwością ich edycji oraz kasowania
            </div>
        );
    }
}

export default UserProfilePage;