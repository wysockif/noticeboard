import axios from 'axios';
import * as apiCalls from './apiCalls';

describe('apiCalls', () => {
    describe('register', () => {
        it('calls /api/1.0/users', () => {
            // given
            const mockRegister = jest.fn();
            axios.post = mockRegister;
            // when
            apiCalls.register();
            // then
            const calledPath = mockRegister.mock.calls[0][0];
            expect(calledPath).toBe('/api/1.0/users');
        });

    });
});