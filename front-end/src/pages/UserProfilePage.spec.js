import React from 'react';
import {render, waitFor} from '@testing-library/react';
import UserProfilePage from './UserProfilePage';
import * as apiCalls from '../api/apiCalls';
import {MemoryRouter} from "react-router-dom";

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

const mockFailResponse = {
    response: {
        status: 404,
        data: {
            message: 'User not found'
        }
    }
};

const mockMatch = {
    params: {
        username: 'username1'
    }
};

const renderProfilePageInRouter = props => {
    return render(
        <MemoryRouter>
            <UserProfilePage {...props}/>
        </MemoryRouter>
    );
};

describe('UserProfilePage', () => {
    it('has div with test id userprofilepage', () => {
        // given
        const {queryByTestId} = renderProfilePageInRouter();
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
        renderProfilePageInRouter({match: mockMatch});
        // then
        expect(mockGetUser).toHaveBeenCalledWith(mockCorrectResponse.data.username);
    });

    it('displays first name and last name when user data loaded', async () => {
        // given
        apiCalls.getUser = jest.fn().mockResolvedValue(mockCorrectResponse);
        // when
        const {queryByText, findByText} = renderProfilePageInRouter({match: mockMatch});
        await waitFor(() => findByText('First Last'));
        // then
        const userFirstAndLastName = queryByText('First Last');
        expect(userFirstAndLastName).toBeInTheDocument();
    });

    it('displays ErrorAlert when user not found', async () => {
        // given
        apiCalls.getUser = jest.fn().mockRejectedValue(mockFailResponse);
        // when
        const {findByTestId, queryByTestId} = renderProfilePageInRouter({match: mockMatch});
        await waitFor(() => findByTestId('error-alert'));
        // then
        const errorDiv = queryByTestId('error-alert');
        expect(errorDiv).toBeInTheDocument();
    });
})