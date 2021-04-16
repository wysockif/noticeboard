import React from 'react';
import { render } from '@testing-library/react';
import UserProfilePage from './UserProfilePage';


describe('UserProfilePage', () => {
    it('has div with test id userprofilepage', () => {
        // given
        const { queryByTestId } = render(<UserProfilePage />);
        // when
        const userProfileDiv = queryByTestId('userprofilepage');
        // then
        expect(userProfileDiv).toBeInTheDocument();
    });
})