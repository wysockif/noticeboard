import React from 'react';
import {render, waitFor} from '@testing-library/react';
import UserProfilePage from './UserProfilePage';
import * as apiCalls from '../api/apiCalls';

const mockCorrectResponse = {
    data: {
        id: 4,
        firstName: 'First',
        lastName: 'Last',
        email: 'email@mail.com',
        username: 'username1',
        image: 'testImage.png'
    }
};

const mockMatch = {
    params: {
        username: 'username1'
    }
};

describe('UserProfilePage', () => {
    it('has div with test id userprofilepage', () => {
        // given
        const {queryByTestId} = render(<UserProfilePage/>);
        // when
        const userProfileDiv = queryByTestId('userprofilepage');
        // then
        expect(userProfileDiv).toBeInTheDocument();
    });

    it('makes an api call to load user details', () => {
        // given
        const mockGetUser = jest.fn().mockResolvedValue(mockCorrectResponse)
        apiCalls.getUser = mockGetUser;
        // when
        render(<UserProfilePage match={mockMatch}/>);
        // then
        expect(mockGetUser).toHaveBeenCalledWith(mockCorrectResponse.data.username);
    });

    it('displays first name and last name when user data loaded', async () => {
        // given
        apiCalls.getUser = jest.fn().mockResolvedValue(mockCorrectResponse);
        // when
        const {queryByText, findByText} = render(<UserProfilePage match={mockMatch}/>);
        await waitFor(() => findByText('First Last'));
        // then
        const userFirstAndLastName = queryByText('First Last');
        expect(userFirstAndLastName).toBeInTheDocument();
    });
})