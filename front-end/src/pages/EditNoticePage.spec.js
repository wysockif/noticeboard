import React from 'react';
import {render} from '@testing-library/react';
import EditNoticePage from './EditNoticePage';


describe('EditNoticePage', () => {
    it('has div with test id editnoticepage', () => {
        // given
        const {queryByTestId} = render(<EditNoticePage/>);
        // when
        const noticeformDiv = queryByTestId('editnoticepage');
        // then
        expect(noticeformDiv).toBeInTheDocument();
    });
})