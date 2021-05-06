import React from 'react';
import {render} from '@testing-library/react';
import NoticePage from './NoticePage';
import {MemoryRouter} from "react-router-dom";
import {createStore} from "redux";
import authenticationReducer from "../redux/authenticationReducer";
import {Provider} from "react-redux";


describe('NoticePage', () => {
    it('has div with test id noticepage', () => {
        // given
        let store = createStore(authenticationReducer);
        const {queryByTestId} = render(
            <Provider store={store}>
                <MemoryRouter>
                    <NoticePage/>
                </MemoryRouter>
            </Provider>
        );
        // when
        const noticeDiv = queryByTestId('noticepage');
        // then
        expect(noticeDiv).toBeInTheDocument();
    });
})