'use client';
import { useSearchParams } from 'next/navigation';
import { players, playerInterface, isAPlayer } from '../../../../data/players';

interface pageProps{
  params: {id: string}
}

export default function PlayerPage({params}: pageProps){
  const player = players.get(params.id);
  if(!isAPlayer(player)) {
    return <div>PLAYER NOT FOUND!</div>;
  }
  const PERCENT_GAME_WINS = Math.round(player.games_won / (player.games_won + player.games_lost) * 100);
  const PERCENT_SERIES_WINS = Math.round(player.series_won / (player.series_won + player.series_lost) * 100);
  const PERCENT_POINT_WINS = Math.round(player.points_won / (player.points_won + player.points_lost) * 100);
  return (
    <div>
      <h1 className="text-center text-xl font-bold mb-10">{player.player_name}'s Stats</h1>
      <div className="flex justify-around">
        <StatMeter percent={PERCENT_GAME_WINS} label="Game Wins" />
        <StatMeter percent={PERCENT_SERIES_WINS} label="Series Wins" />
        <StatMeter percent={PERCENT_POINT_WINS} label="Point Wins" />
      </div>
    </div>
  )
}

function StatMeter({percent, label}:{percent: number, label: string}){
  return <div>{label}: {percent}%</div>
}