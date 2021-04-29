import React from 'react';
import {render} from '@testing-library/react';
import CreateNoticePage from './CreateNoticePage';
import {createStore} from "redux";
import authenticationReducer from "../redux/authenticationReducer";
import {Provider} from "react-redux";
import {MemoryRouter} from "react-router-dom";


describe('CreateNoticePage', () => {
    it('has div with test id createnoticepage', () => {
        // given
        let store = createStore(authenticationReducer);
        const {queryByTestId} = render(
            <Provider store={store}>
                <MemoryRouter>
                    <CreateNoticePage/>
                </MemoryRouter>
            </Provider>
        );
        // when
        const createNoticeDiv = queryByTestId('createnoticepage');
        // then
        expect(createNoticeDiv).toBeInTheDocument();
    });
});