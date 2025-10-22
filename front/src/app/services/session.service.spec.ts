import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { SessionService } from './session.service';
import { SessionInformation } from '../interfaces/sessionInformation.interface';


describe('SessionService', () => {
   let service: SessionService;

    const fakeUser: SessionInformation = {
      id: 1,
      username: 'test1',
      firstName: 'Test',
      lastName: 'test',
      admin: true,
      token: 'fake-token',
      type: 'USER'
    };

    beforeEach(() => {
      TestBed.configureTestingModule({});
      service = TestBed.inject(SessionService);
    });

    it('should be created', () => {
      expect(service).toBeTruthy();
    });

    it('should start logged out', () => {
      expect(service.isLogged).toBe(false);
      expect(service.sessionInformation).toBeUndefined();
    });

    it('should log in the user and update state', () => {
      service.logIn(fakeUser);

      expect(service.sessionInformation).toEqual(fakeUser);
      expect(service.isLogged).toBe(true);
    });

    it('should log out the user and clear session', () => {
      service.logIn(fakeUser);
      service.logOut();

      expect(service.isLogged).toBe(false);
      expect(service.sessionInformation).toBeUndefined();
    });

    it('should emit isLogged changes through the observable', (done) => {
      const values: boolean[] = [];

      service.$isLogged().subscribe((val) => {
        values.push(val);
        // On 3rd emission, we can verify results
        if (values.length === 3) {
          expect(values).toEqual([false, true, false]);
          done();
        }
      });

      service.logIn(fakeUser);
      service.logOut();
    });
});
