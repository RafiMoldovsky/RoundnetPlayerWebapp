export const players:Map<string, playerInterface> = new Map([
  ["1", {
    id: "1",
    player_name: 'Rafi Moldovsky',
    games_won: 8,
    games_lost: 9,
    series_won: 1,
    series_lost: 2,
    points_won: 323,
    points_lost: 334,
  }],
  ["2", {
    id: "2",
    player_name: 'Kyan Kornfeld',
    games_won: 8,
    games_lost: 9,
    series_won: 1,
    series_lost: 2,
    points_won: 323,
    points_lost: 334,
  }],
  ["3", {
    id: "3",
    player_name: 'William Foote',
    games_won: 8,
    games_lost: 9,
    series_won: 1,
    series_lost: 2,
    points_won: 323,
    points_lost: 334,
  }], 
  ["4", {
    id: "4",
    player_name: 'Arrow Griner',
    games_won: 8,
    games_lost: 9,
    series_won: 1,
    series_lost: 2,
    points_won: 323,
    points_lost: 334,
  }], 
]);

export interface playerInterface {
  id: string;
  player_name: string;
  games_won: number;
  games_lost: number;
  series_won: number;
  series_lost: number;
  points_won: number;
  points_lost: number;
}

export function isAPlayer(obj: any): obj is playerInterface {
  if (typeof obj === 'undefined'){return false};
  return 'id' in obj && 'player_name' in obj && 'games_won' in obj && 'games_lost' in obj && 'series_won' in obj && 'series_lost' in obj && 'points_won' in obj && 'points_lost' in obj;
}