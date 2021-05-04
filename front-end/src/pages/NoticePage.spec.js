import React from 'react';
import {render} from '@testing-library/react';
import NoticePage from './NoticePage';
import {MemoryRouter} from "react-router-dom";


describe('NoticePage', () => {
    it('has div with test id noticepage', () => {
        // given
        const {queryByTestId} = render(
            <MemoryRouter>
                <NoticePage/>
            </MemoryRouter>
        );
        // when
        const noticeDiv = queryByTestId('noticepage');
        // then
        expect(noticeDiv).toBeInTheDocument();
    });
})